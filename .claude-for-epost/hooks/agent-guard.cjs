#!/usr/bin/env node
/**
 * agent-guard.cjs - Block dispatch of unknown/general-purpose agents
 *
 * Fires: PreToolUse on Agent tool calls
 * Purpose: Ensure Claude uses only .claude/agents/* or known plugin agents.
 *          Generic fallback agents (general-purpose, claude, Explore, Plan)
 *          require explicit user approval before dispatch.
 *
 * Flow:
 * 1. Claude tries Agent({ subagent_type: "general-purpose" }) → BLOCKED
 * 2. Claude uses AskUserQuestion to ask user for approval
 * 3. User approves → Claude runs: node .claude/hooks/agent-guard.cjs --approve general-purpose
 * 4. Claude retries Agent({ subagent_type: "general-purpose" }) → ALLOWED (5 min window)
 *
 * Sub-commands:
 *   node agent-guard.cjs --approve <agent_type>   Write a timed approval token
 *   node agent-guard.cjs --revoke  <agent_type>   Remove an approval token
 *   node agent-guard.cjs --list                   List active approvals
 *
 * Exit Codes:
 *   0 - Agent allowed
 *   2 - Agent blocked (with instructions for Claude to ask user)
 */

'use strict';

const fs   = require('fs');
const path = require('path');
const os   = require('os');

// ─── Config ──────────────────────────────────────────────────────────────────

const APPROVAL_FILE  = path.join(os.tmpdir(), 'claude-agent-guard-approved.json');
const APPROVAL_TTL   = 5 * 60 * 1000; // 5 minutes

// Generic/fallback agents that always require approval
const BLOCKED_AGENTS = new Set([
  'general-purpose',
  'claude',
  'Explore',
  'Plan',
]);

// Specialized plugin agents allowed without local definition
const ALLOWED_PLUGIN_AGENTS = new Set([
  'caveman:cavecrew-investigator',
  'caveman:cavecrew-builder',
  'caveman:cavecrew-reviewer',
  'caveman:caveman',
  'claude-code-guide',
  'statusline-setup',
  // aws-dev-toolkit
  'aws-dev-toolkit:agentcore-sme',
  'aws-dev-toolkit:aws-explorer',
  'aws-dev-toolkit:bedrock-sme',
  'aws-dev-toolkit:container-sme',
  'aws-dev-toolkit:cost-optimizer',
  'aws-dev-toolkit:iac-reviewer',
  'aws-dev-toolkit:migration-advisor',
  'aws-dev-toolkit:networking-sme',
  'aws-dev-toolkit:observability-sme',
  'aws-dev-toolkit:serverless-sme',
  'aws-dev-toolkit:well-architected-reviewer',
  // vercel
  'vercel:ai-architect',
  'vercel:deployment-expert',
  'vercel:performance-optimizer',
]);

// ─── Approval store (temp file, TTL-scoped) ───────────────────────────────────

function readApprovals() {
  try {
    const raw = fs.readFileSync(APPROVAL_FILE, 'utf-8');
    const data = JSON.parse(raw);
    const now = Date.now();
    return Array.isArray(data) ? data.filter(a => a.expires > now) : [];
  } catch { return []; }
}

function writeApprovals(list) {
  fs.writeFileSync(APPROVAL_FILE, JSON.stringify(list, null, 2));
}

function isApproved(agentType) {
  return readApprovals().some(a => a.agent_type === agentType);
}

// ─── Local agent discovery ────────────────────────────────────────────────────

function getLocalAgentNames() {
  try {
    const agentsDir = path.join(path.dirname(__dirname), 'agents');
    if (!fs.existsSync(agentsDir)) return new Set();

    const names = new Set();
    for (const file of fs.readdirSync(agentsDir)) {
      if (!file.endsWith('.md')) continue;
      const content = fs.readFileSync(path.join(agentsDir, file), 'utf-8');
      const m = content.match(/^---[\s\S]*?^name:\s*(.+)$/m);
      names.add(m ? m[1].trim() : path.basename(file, '.md'));
    }
    return names;
  } catch { return new Set(); }
}

// ─── Block message ────────────────────────────────────────────────────────────

function formatBlockMessage(agentType) {
  const promptData = {
    type: 'AGENT_GUARD_PROMPT',
    agent_type: agentType,
    question: {
      header: 'Agent Dispatch',
      text: `"${agentType}" is a generic/unknown agent not in .claude/agents/*. Allow this dispatch?`,
      options: [
        {
          label: 'Yes, approve',
          description: `Allow ${agentType} for 5 min. Claude will run the approve command then retry.`
        },
        {
          label: 'No, use defined agent',
          description: 'Claude will pick a suitable agent from .claude/agents/ instead.'
        }
      ]
    }
  };

  return `
\x1b[36mNOTE:\x1b[0m Enforcing agent routing discipline — this is not an error.

\x1b[33mAGENT GUARD\x1b[0m: Unknown agent dispatch blocked

  \x1b[33mAgent:\x1b[0m ${agentType}
  \x1b[33mReason:\x1b[0m Not in .claude/agents/* or allowed plugin list

\x1b[90m@@AGENT_GUARD_PROMPT_START@@\x1b[0m
${JSON.stringify(promptData, null, 2)}
\x1b[90m@@AGENT_GUARD_PROMPT_END@@\x1b[0m

  \x1b[34mClaude instructions:\x1b[0m
  1. Use AskUserQuestion with the JSON above to ask the user.
  2. \x1b[32mIf "Yes":\x1b[0m  Run: node .claude/hooks/agent-guard.cjs --approve ${agentType}
               Then retry the Agent dispatch.
  3. \x1b[31mIf "No":\x1b[0m   Use an appropriate agent from .claude/agents/ instead.
`;
}

// ─── Sub-commands ─────────────────────────────────────────────────────────────

function cmdApprove(agentType) {
  const approvals = readApprovals().filter(a => a.agent_type !== agentType);
  approvals.push({ agent_type: agentType, expires: Date.now() + APPROVAL_TTL });
  writeApprovals(approvals);
  console.log(`\x1b[32m✓\x1b[0m Agent "${agentType}" approved for 5 minutes`);
  process.exit(0);
}

function cmdRevoke(agentType) {
  const approvals = readApprovals().filter(a => a.agent_type !== agentType);
  writeApprovals(approvals);
  console.log(`\x1b[33m✓\x1b[0m Approval revoked for "${agentType}"`);
  process.exit(0);
}

function cmdList() {
  const approvals = readApprovals();
  if (!approvals.length) {
    console.log('No active agent approvals.');
  } else {
    console.log('Active agent approvals:');
    for (const a of approvals) {
      const remaining = Math.round((a.expires - Date.now()) / 1000);
      console.log(`  ${a.agent_type}  (expires in ${remaining}s)`);
    }
  }
  process.exit(0);
}

// ─── Main hook logic ──────────────────────────────────────────────────────────

function main() {
  // Handle sub-commands when called directly from bash
  const [,, subcmd, arg] = process.argv;
  if (subcmd === '--approve' && arg) return cmdApprove(arg);
  if (subcmd === '--revoke'  && arg) return cmdRevoke(arg);
  if (subcmd === '--list')           return cmdList();

  // --- PreToolUse hook mode ---
  let input = '';
  try { input = fs.readFileSync(0, 'utf-8'); } catch { process.exit(0); }

  let hookData;
  try { hookData = JSON.parse(input); } catch { process.exit(0); }

  const toolName  = hookData.tool_name  || '';
  const toolInput = hookData.tool_input || {};

  // Only intercept Agent tool calls
  if (toolName !== 'Agent') process.exit(0);

  // No subagent_type = implicit general-purpose default
  const agentType = (toolInput.subagent_type || 'general-purpose').trim();

  // Check temp-file approval first (user already approved this session)
  if (isApproved(agentType)) process.exit(0);

  // Allow local .claude/agents/* agents
  const localAgents = getLocalAgentNames();
  if (localAgents.has(agentType)) process.exit(0);

  // Allow explicitly allowed plugin agents
  if (ALLOWED_PLUGIN_AGENTS.has(agentType)) process.exit(0);

  // Allow any namespaced (plugin) agent not in the blocked list
  // e.g. aws-dev-toolkit:new-agent, vercel:new-agent
  if (agentType.includes(':') && !BLOCKED_AGENTS.has(agentType)) process.exit(0);

  // Block — output prompt for Claude to ask user
  console.error(formatBlockMessage(agentType));
  process.exit(2);
}

// ─── Entry point ─────────────────────────────────────────────────────────────

try {
  main();
} catch (e) {
  try {
    const logDir = path.join(__dirname, '.logs');
    if (!fs.existsSync(logDir)) fs.mkdirSync(logDir, { recursive: true });
    fs.appendFileSync(
      path.join(logDir, 'hook-log.jsonl'),
      JSON.stringify({ ts: new Date().toISOString(), hook: 'agent-guard', status: 'crash', error: e.message }) + '\n'
    );
  } catch (_) {}
  process.exit(0); // fail-open
}
