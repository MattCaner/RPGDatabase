
--ten plik utworzy w schemie projekt baze danych projektu
--

CREATE SEQUENCE projekt.log_id_akcji_seq;

CREATE TABLE projekt.log (
                id_akcji INTEGER NOT NULL DEFAULT nextval('projekt.log_id_akcji_seq'),
                time TIME NOT NULL,
                typ_akcji VARCHAR NOT NULL,
                date DATE NOT NULL,
                CONSTRAINT log_pk PRIMARY KEY (id_akcji)
);


ALTER SEQUENCE projekt.log_id_akcji_seq OWNED BY projekt.log.id_akcji;

CREATE TABLE projekt.uprawnienia (
                id INTEGER NOT NULL,
                nazwa VARCHAR NOT NULL,
                CONSTRAINT uprawnienia_pk PRIMARY KEY (id)
);


CREATE SEQUENCE projekt.rasa_id_rasa_seq;

CREATE TABLE projekt.rasa (
                id_rasa INTEGER NOT NULL DEFAULT nextval('projekt.rasa_id_rasa_seq'),
                nazwa_rasa VARCHAR NOT NULL UNIQUE,
                cechy VARCHAR NOT NULL,
                opis VARCHAR,
                CONSTRAINT rasa_pk PRIMARY KEY (id_rasa)
);


ALTER SEQUENCE projekt.rasa_id_rasa_seq OWNED BY projekt.rasa.id_rasa;

CREATE SEQUENCE projekt.przedmiot_id_przedmiotu_seq;

CREATE TABLE projekt.przedmiot (
                id_przedmiotu INTEGER NOT NULL DEFAULT nextval('projekt.przedmiot_id_przedmiotu_seq'),
                nazwa_przedmiotu VARCHAR NOT NULL UNIQUE,
                typ VARCHAR NOT NULL,
                dziaanie VARCHAR NOT NULL,
                rzadkosc VARCHAR,
                uwagi VARCHAR,
                wartosc_sp NUMERIC NOT NULL,
                CONSTRAINT przedmiot_id PRIMARY KEY (id_przedmiotu)
);


ALTER SEQUENCE projekt.przedmiot_id_przedmiotu_seq OWNED BY projekt.przedmiot.id_przedmiotu;

CREATE SEQUENCE projekt.swiat_id_swiat_seq_1;

CREATE TABLE projekt.swiat (
                id_swiat INTEGER NOT NULL DEFAULT nextval('projekt.swiat_id_swiat_seq_1'),
                nazwa VARCHAR NOT NULL UNIQUE,
                krotki_opis VARCHAR NOT NULL,
                CONSTRAINT swiat_id PRIMARY KEY (id_swiat)
);


ALTER SEQUENCE projekt.swiat_id_swiat_seq_1 OWNED BY projekt.swiat.id_swiat;

CREATE SEQUENCE projekt.miejsce_id_miejsca_seq;

CREATE TABLE projekt.miejsce (
                id_miejsca INTEGER NOT NULL DEFAULT nextval('projekt.miejsce_id_miejsca_seq'),
                nazwa_miejsca VARCHAR NOT NULL UNIQUE,
                adres VARCHAR NOT NULL,
                CONSTRAINT miejsce_id PRIMARY KEY (id_miejsca)
);


ALTER SEQUENCE projekt.miejsce_id_miejsca_seq OWNED BY projekt.miejsce.id_miejsca;

CREATE SEQUENCE projekt.grupa_id_grupa_seq;

CREATE TABLE projekt.grupa (
                id_grupa INTEGER NOT NULL DEFAULT nextval('projekt.grupa_id_grupa_seq'),
                nazwa VARCHAR NOT NULL UNIQUE,
                uwagi VARCHAR,
                CONSTRAINT grupa_id PRIMARY KEY (id_grupa)
);


ALTER SEQUENCE projekt.grupa_id_grupa_seq OWNED BY projekt.grupa.id_grupa;

CREATE SEQUENCE projekt.czlowiek_id_czlowiek_seq;

CREATE TABLE projekt.czlowiek (
                id_czlowiek INTEGER NOT NULL DEFAULT nextval('projekt.czlowiek_id_czlowiek_seq'),
                imie VARCHAR,
                nick VARCHAR NOT NULL UNIQUE,
                CONSTRAINT czlowiek_id PRIMARY KEY (id_czlowiek)
);


ALTER SEQUENCE projekt.czlowiek_id_czlowiek_seq OWNED BY projekt.czlowiek.id_czlowiek;

CREATE TABLE projekt.czlowiek_uprawnienia (
                id INTEGER NOT NULL,
                id_czlowiek INTEGER NOT NULL,
                CONSTRAINT czlowiek_uprawnienia_pk PRIMARY KEY (id, id_czlowiek)
);


CREATE TABLE projekt.passwd (
                id_czlowiek INTEGER NOT NULL,
                hash VARCHAR NOT NULL,
                CONSTRAINT passwd_pk PRIMARY KEY (id_czlowiek)
);


CREATE SEQUENCE projekt.kampania_id_kampania_seq_2;

CREATE TABLE projekt.kampania (
                id_kampania INTEGER NOT NULL DEFAULT nextval('projekt.kampania_id_kampania_seq_2'),
                nazwa VARCHAR NOT NULL,
                id_swiat INTEGER NOT NULL,
                id_czlowiek_autor INTEGER NOT NULL,
                CONSTRAINT kampania_id PRIMARY KEY (id_kampania)
);


ALTER SEQUENCE projekt.kampania_id_kampania_seq_2 OWNED BY projekt.kampania.id_kampania;

CREATE SEQUENCE projekt.sesja_id_sesja_seq;

CREATE TABLE projekt.sesja (
                id_sesja INTEGER NOT NULL DEFAULT nextval('projekt.sesja_id_sesja_seq'),
                id_kampania INTEGER NOT NULL,
                id_czlowiek_mg INTEGER NOT NULL,
                id_miejsca INTEGER NOT NULL,
                CONSTRAINT sesja_id PRIMARY KEY (id_sesja)
);


ALTER SEQUENCE projekt.sesja_id_sesja_seq OWNED BY projekt.sesja.id_sesja;

CREATE TABLE projekt.swiat_autor (
                id_swiat INTEGER NOT NULL,
                id_czlowiek INTEGER NOT NULL,
                rola VARCHAR NOT NULL,
                CONSTRAINT swiat_autor_pk PRIMARY KEY (id_swiat, id_czlowiek)
);


CREATE SEQUENCE projekt.pc_id_postac_seq;

CREATE TABLE projekt.pc (
                id_postac INTEGER NOT NULL DEFAULT nextval('projekt.pc_id_postac_seq'),
                czlowiek_id INTEGER NOT NULL,
                imie VARCHAR NOT NULL UNIQUE,
                id_kampania INTEGER NOT NULL,
                klasa VARCHAR NOT NULL,
                poziom INTEGER NOT NULL,
                sia INTEGER NOT NULL,
                zrczno INTEGER NOT NULL,
                mdro INTEGER NOT NULL,
                inteligencja INTEGER NOT NULL,
                charyzma INTEGER NOT NULL,
                kondycja INTEGER NOT NULL,
                charakter VARCHAR NOT NULL,
                pw_max INTEGER NOT NULL,
                pw_aktualne INTEGER,
                kp INTEGER NOT NULL,
                id_rasa INTEGER NOT NULL,
                komrki_zakl VARCHAR NOT NULL,
                CONSTRAINT pc_id PRIMARY KEY (id_postac)
);


ALTER SEQUENCE projekt.pc_id_postac_seq OWNED BY projekt.pc.id_postac;

CREATE TABLE projekt.udzial_w_sesji (
                id_postac INTEGER NOT NULL,
                id_sesja INTEGER NOT NULL,
                CONSTRAINT udzial_w_sesji_pk PRIMARY KEY (id_postac, id_sesja)
);


CREATE TABLE projekt.przedmiot_postac (
                id_postac INTEGER NOT NULL,
                id_przedmiotu INTEGER NOT NULL,
                ilosc INTEGER NOT NULL,
                umie_uzywac BOOLEAN NOT NULL,
                CONSTRAINT przedmiot_postac_pk PRIMARY KEY (id_postac, id_przedmiotu)
);


CREATE TABLE projekt.gracz_grupa (
                id_grupa INTEGER NOT NULL,
                id_czlowiek INTEGER NOT NULL,
                CONSTRAINT gracz_grupa_pk PRIMARY KEY (id_grupa, id_czlowiek)
);


ALTER TABLE projekt.czlowiek_uprawnienia ADD CONSTRAINT uprawnienia_czlowiek_uprawnienia_fk
FOREIGN KEY (id)
REFERENCES projekt.uprawnienia (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.pc ADD CONSTRAINT rasa_pc_fk
FOREIGN KEY (id_rasa)
REFERENCES projekt.rasa (id_rasa)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.przedmiot_postac ADD CONSTRAINT przedmiot_przedmiot_postac_fk
FOREIGN KEY (id_przedmiotu)
REFERENCES projekt.przedmiot (id_przedmiotu)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.kampania ADD CONSTRAINT swiat_kampania_fk
FOREIGN KEY (id_swiat)
REFERENCES projekt.swiat (id_swiat)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.swiat_autor ADD CONSTRAINT swiat_swiat_autor_fk
FOREIGN KEY (id_swiat)
REFERENCES projekt.swiat (id_swiat)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.sesja ADD CONSTRAINT miejsce_sesja_fk
FOREIGN KEY (id_miejsca)
REFERENCES projekt.miejsce (id_miejsca)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.gracz_grupa ADD CONSTRAINT grupa_gracz_grupa_fk
FOREIGN KEY (id_grupa)
REFERENCES projekt.grupa (id_grupa)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.gracz_grupa ADD CONSTRAINT czlowiek_gracz_grupa_fk
FOREIGN KEY (id_czlowiek)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.pc ADD CONSTRAINT czlowiek_pc_fk
FOREIGN KEY (czlowiek_id)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.swiat_autor ADD CONSTRAINT czlowiek_swiat_autor_fk
FOREIGN KEY (id_czlowiek)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.sesja ADD CONSTRAINT czlowiek_sesja_fk
FOREIGN KEY (id_czlowiek_mg)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.kampania ADD CONSTRAINT czlowiek_kampania_fk
FOREIGN KEY (id_czlowiek_autor)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.passwd ADD CONSTRAINT czlowiek_passwd_fk
FOREIGN KEY (id_czlowiek)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.czlowiek_uprawnienia ADD CONSTRAINT czlowiek_czlowiek_uprawnienia_fk
FOREIGN KEY (id_czlowiek)
REFERENCES projekt.czlowiek (id_czlowiek)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.sesja ADD CONSTRAINT kampania_sesja_fk
FOREIGN KEY (id_kampania)
REFERENCES projekt.kampania (id_kampania)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.pc ADD CONSTRAINT kampania_pc_fk
FOREIGN KEY (id_kampania)
REFERENCES projekt.kampania (id_kampania)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.udzial_w_sesji ADD CONSTRAINT sesja_udzial_w_sesji_fk
FOREIGN KEY (id_sesja)
REFERENCES projekt.sesja (id_sesja)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.przedmiot_postac ADD CONSTRAINT pc_przedmiot_postac_fk
FOREIGN KEY (id_postac)
REFERENCES projekt.pc (id_postac)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE projekt.udzial_w_sesji ADD CONSTRAINT pc_udzial_w_sesji_fk
FOREIGN KEY (id_postac)
REFERENCES projekt.pc (id_postac)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;