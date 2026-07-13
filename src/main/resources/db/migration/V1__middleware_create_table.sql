-- This will safely skip creation if the sequence is already sitting in the schema
CREATE SEQUENCE IF NOT EXISTS customer_code_seq START WITH 1001;

CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Auto-generated integer or prefix string backed by a sequence
    customer_code VARCHAR(50) UNIQUE NOT NULL DEFAULT 'CUST-' || nextval('customer_code_seq'),

    customer_type VARCHAR(20) NOT NULL, -- 'INDIVIDUAL' or 'BUSINESS'
    name VARCHAR(255) NOT NULL,          -- Contact Person Name
    company_name VARCHAR(255),           -- Nullable if Individual
    tin VARCHAR(50),                     -- Tax Identification Number

    -- Address Details
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'Nigeria',

    email VARCHAR(100),
    phone_number VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,

    -- Audit Fields from Auditable base class
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 2. ITEMS SETUP
CREATE TABLE items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    unit_price NUMERIC(15, 4) NOT NULL,
    tax_rate NUMERIC(5, 2) DEFAULT 0.00,
    -- Audit Fields from Base Class
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 3. INVOICES (Header)
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(100) UNIQUE NOT NULL,
    customer_id UUID NOT NULL REFERENCES customers(id),
    status VARCHAR(30) NOT NULL,
    gross_amount NUMERIC(15, 4) NOT NULL DEFAULT 0.0000,
    tax_amount NUMERIC(15, 4) NOT NULL DEFAULT 0.0000,
    net_amount NUMERIC(15, 4) NOT NULL DEFAULT 0.0000,
    -- Audit Fields from Base Class
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 4. INVOICE LINES (Details)
-- Note: It is optional to audit lines if auditing the Header covers business requirements,
-- but adding it guarantees deep transactional transparency.
CREATE TABLE invoice_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES items(id),
    quantity NUMERIC(12, 4) NOT NULL,
    unit_price NUMERIC(15, 4) NOT NULL,
    line_tax_amount NUMERIC(15, 4) NOT NULL,
    line_net_amount NUMERIC(15, 4) NOT NULL,
    -- Audit Fields from Base Class
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 5. PAYMENTS
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    payment_reference VARCHAR(100) UNIQUE NOT NULL,
    amount_paid NUMERIC(15, 4) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    -- Audit Fields from Base Class
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 6. AUDIT LOGS (Remains unchanged as your centralized JSON change log)
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_name VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL,
    performed_by UUID NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    old_values JSONB,
    new_values JSONB
);