# notes - metadata csv import

task: import_metadata.py reads a CSV (asset_id, title, keywords, rights_expiry) and updates asset metadata. keywords are ; separated. rights_expiry is YYYY-MM-DD.

prompt 1: "context: import_metadata.py, sample.csv, task readme. give me a plan, dont write code yet"
- plan was fine, kept it. it suggested a dataclass per row, kept

prompt 2: "implement parse_rows from the plan"
- agent parsed each line with line.split(",")
- REJECTED: titles have quoted commas, sample.csv row 2 is "Sunset, Bali" -> would shift every column
- told it to use csv.DictReader instead

prompt 3: "validate rights_expiry"
- agent skipped rows with bad dates silently (just `continue`)
- CHANGED: now collects errors with row number + reason and returns an ImportReport, nothing dropped silently. user needs to know which assets didnt update

tests (pytest), all 4 pass:
- quoted comma in title parses as one field
- empty keywords -> []
- bad date on row 3 -> in report errors, other rows still imported
- unknown asset_id -> in report errors

not done:
- only tried sample.csv (50 rows), no big file / streaming check
- keywords not deduped
