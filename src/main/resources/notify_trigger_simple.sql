CREATE OR REPLACE FUNCTION notify_change() RETURNS TRIGGER AS $$
    BEGIN
        PERFORM pg_notify('db_notifications', TG_OP  || '.' || TG_TABLE_NAME);
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;