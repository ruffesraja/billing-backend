-- Add signature_base64 column to owners table
ALTER TABLE owners ADD COLUMN signature_base64 TEXT;

-- Add comment to the column
COMMENT ON COLUMN owners.signature_base64 IS 'Signature image stored as base64 encoded string';
