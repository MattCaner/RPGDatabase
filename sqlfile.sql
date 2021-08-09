insert into uprawnienia values
(0, 'admin'),
(1, 'wb'),
(2, 'mg'),
(3, 'player');

insert into projekt.czlowiek (nick) values ('Surt');

insert into projekt.passwd(id_czlowiek,hash) values (1,'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3');

insert into projekt.czlowiek_uprawnienia values
(0,1),(1,1),(2,1),(3,1);

insert into rasa (nazwa_rasa, cechy, opis) values ('Czlowiek','+1 do wszystkich cech','...');

('Elf leśny','+2 do zręczności, +1 do mądrości','Leśne elfy są pełne wdzięku, mają wyostrzone zmysły i intuicję.');

CREATE OR REPLACE FUNCTION audit_log ()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    AS $$
    BEGIN
 
    INSERT INTO projekt.log (_time, typ_akcji,_date) 
        VALUES (CURRENT_TIME, TG_OP,CURRENT_DATE);
 
    RETURN NEW;                                                          
    END;
    $$;


CREATE TRIGGER passwd_audit 
    AFTER SELECT OR INSERT ON projekt.passwd
    FOR EACH ROW EXECUTE PROCEDURE audit_log();  