CREATE TABLE "person" (
  "id" serial PRIMARY KEY,
  "email" varchar UNIQUE NOT NULL,
  "password" varchar NOT NULL,
  "username" varchar NOT NULL,
  "link" varchar UNIQUE NOT NULL,
  "is_enabled" boolean NOT NULL
);

CREATE TABLE "confirmation_token" (
  "id" serial PRIMARY KEY,
  "user_id" integer NOT NULL,
  "token" varchar NOT NULL,
  "created_at" timestamp NOT NULL,
  "expires_at" timestamp NOT NULL,
  "confirmed_at" timestamp
);

CREATE TABLE "meeting_type" (
  "id" serial PRIMARY KEY,
  "title" varchar NOT NULL,
  "duration" integer NOT NULL,
  "description" varchar,
  "created_at" timestamp NOT NULL,
  "max_participants" integer NOT NULL,
  "location_id" integer NOT NULL,
  "creator_id" integer NOT NULL,
  "is_deleted" boolean NOT NULL
);

CREATE TABLE "location" (
  "id" serial PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE "meeting_type_time_slot" (
  "id" serial PRIMARY KEY,
  "event_type_id" integer NOT NULL,
  "begin_at" timestamp NOT NULL,
  "end_at" timestamp NOT NULL
);

CREATE TABLE "meeting_poll_time_slot" (
  "id" serial PRIMARY KEY,
  "meeting_poll_id" integer NOT NULL,
  "begin_at" timestamp NOT NULL,
  "end_at" timestamp NOT NULL
);

CREATE TABLE "meeting_poll" (
  "id" serial PRIMARY KEY,
  "title" varchar NOT NULL,
  "duration" integer NOT NULL,
  "description" varchar,
  "created_at" timestamp NOT NULL,
  "creator_id" integer NOT NULL,
  "location_id" integer NOT NULL,
  "is_active" boolean NOT NULL
);

CREATE TABLE "meeting_type_scheduled_event" (
  "id" serial PRIMARY KEY,
  "comment" varchar,
  "link" varchar,
  "time_slot_id" integer,
  "is_passed" boolean NOT NULL
);

CREATE TABLE "meeting_poll_scheduled_event" (
  "id" serial PRIMARY KEY,
  "comment" varchar,
  "link" varchar,
  "time_slot_id" integer
);

CREATE TABLE "meeting_type_participant" (
  "id" serial PRIMARY KEY,
  "event_id" integer NOT NULL,
  "participant_name" varchar NOT NULL,
  "participant_email" varchar NOT NULL,
  "created_at" timestamp NOT NULL
);

CREATE TABLE "meeting_poll_participant" (
  "id" serial PRIMARY KEY,
  "poll_event_id" integer NOT NULL,
  "participant_name" varchar NOT NULL,
  "participant_email" varchar NOT NULL
);

CREATE TABLE "meeting_poll_vote" (
  "id" serial PRIMARY KEY,
  "time_slot_id" integer NOT NULL,
  "registered_name" varchar NOT NULL,
  "registered_email" varchar NOT NULL
);

CREATE TABLE "calendar" (
  "id" serial PRIMARY KEY,
  "calendar_name" varchar NOT NULL
);

CREATE TABLE "connected_calendar" (
  "id" serial PRIMARY KEY,
  "user_id" integer NOT NULL,
  "calendar_id" integer NOT NULL
);

CREATE TABLE "notification" (
  "id" serial PRIMARY KEY,
  "user_id" integer NOT NULL,
  "is_on" boolean NOT NULL
);

ALTER TABLE "meeting_poll" ADD FOREIGN KEY ("creator_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "meeting_type" ADD FOREIGN KEY ("creator_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "connected_calendar" ADD FOREIGN KEY ("user_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "connected_calendar" ADD FOREIGN KEY ("calendar_id") REFERENCES "calendar" ("id") ON DELETE CASCADE;

ALTER TABLE "meeting_type_time_slot" ADD FOREIGN KEY ("event_type_id") REFERENCES "meeting_type" ("id") ON DELETE CASCADE;

ALTER TABLE "meeting_type" ADD FOREIGN KEY ("location_id") REFERENCES "location" ("id");

ALTER TABLE "meeting_poll" ADD FOREIGN KEY ("location_id") REFERENCES "location" ("id");

ALTER TABLE "meeting_type_participant" ADD FOREIGN KEY ("event_id") REFERENCES "meeting_type_scheduled_event" ("id") ON DELETE CASCADE;

ALTER TABLE "notification" ADD FOREIGN KEY ("user_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "confirmation_token" ADD FOREIGN KEY ("user_id") REFERENCES "person" ("id") ON DELETE CASCADE;

ALTER TABLE "meeting_poll_time_slot" ADD FOREIGN KEY ("meeting_poll_id") REFERENCES "meeting_poll" ("id") ON DELETE CASCADE;

ALTER TABLE "meeting_poll_participant" ADD FOREIGN KEY ("poll_event_id") REFERENCES "meeting_poll_scheduled_event" ("id") ON DELETE CASCADE;

ALTER TABLE "meeting_poll_vote" ADD FOREIGN KEY ("time_slot_id") REFERENCES "meeting_poll_time_slot" ("id");

ALTER TABLE "meeting_poll_scheduled_event" ADD FOREIGN KEY ("time_slot_id") REFERENCES "meeting_poll_time_slot" ("id");

ALTER TABLE "meeting_type_scheduled_event" ADD FOREIGN KEY ("time_slot_id") REFERENCES "meeting_type_time_slot" ("id");
