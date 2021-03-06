# --- !Ups

create table dvd (
  id                        bigint auto_increment not null,
  owner_id                  bigint,
  borrower_id               bigint,
  borrow_date               bigint,
  borrower_name             varchar(255),
  hull_nr                   integer,
  movie_id                  bigint,
  created_date              bigint not null,
  constraint pk_dvd primary key (id))
;

create table dvd_attribute (
  id                        bigint auto_increment not null,
  attribute_type            varchar(10) not null,
  value                     varchar(255),
  constraint ck_dvd_attribute_attribute_type check (attribute_type in ('BOX','COLLECTION','RATING','COPY_TYPE')),
  constraint pk_dvd_attribute primary key (id))
;

create table movie (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  has_poster                tinyint(1) default 0,
  has_backdrop              tinyint(1) default 0,
  has_to_be_reviewed        tinyint(1) default 0 not null,
  description               longtext,
  year                      integer not null,
  runtime                   integer,
  trailer_url               varchar(255),
  constraint pk_movie primary key (id))
;

create table movie_attribute (
  pk                        bigint auto_increment not null,
  attribute_type            varchar(12) not null,
  value                     varchar(255),
  constraint ck_movie_attribute_attribute_type check (attribute_type in ('ACTOR','GENRE','DIRECTOR','MOVIE_SERIES')),
  constraint pk_movie_attribute primary key (pk))
;

create table setting (
  id                        bigint auto_increment not null,
  bundle                    varchar(255) not null,
  ke_y                      varchar(255) not null,
  value                     varchar(255),
  constraint pk_setting primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  user_name                 varchar(255),
  password                  varchar(255),
  email                     varchar(255),
  constraint uq_user_user_name unique (user_name),
  constraint pk_user primary key (id))
;


create table dvd_attribute_dvd (
  dvd_attribute_id               bigint not null,
  dvd_id                         bigint not null,
  constraint pk_dvd_attribute_dvd primary key (dvd_attribute_id, dvd_id))
;

create table movie_attribute_movie (
  movie_attribute_pk             bigint not null,
  movie_id                       bigint not null,
  constraint pk_movie_attribute_movie primary key (movie_attribute_pk, movie_id))
;
alter table dvd add constraint fk_dvd_owner_1 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_dvd_owner_1 on dvd (owner_id);
alter table dvd add constraint fk_dvd_borrower_2 foreign key (borrower_id) references user (id) on delete restrict on update restrict;
create index ix_dvd_borrower_2 on dvd (borrower_id);
alter table dvd add constraint fk_dvd_movie_3 foreign key (movie_id) references movie (id) on delete restrict on update restrict;
create index ix_dvd_movie_3 on dvd (movie_id);



alter table dvd_attribute_dvd add constraint fk_dvd_attribute_dvd_dvd_attr_01 foreign key (dvd_attribute_id) references dvd_attribute (id) on delete restrict on update restrict;

alter table dvd_attribute_dvd add constraint fk_dvd_attribute_dvd_dvd_02 foreign key (dvd_id) references dvd (id) on delete restrict on update restrict;

alter table movie_attribute_movie add constraint fk_movie_attribute_movie_movi_01 foreign key (movie_attribute_pk) references movie_attribute (pk) on delete restrict on update restrict;

alter table movie_attribute_movie add constraint fk_movie_attribute_movie_movi_02 foreign key (movie_id) references movie (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table dvd;

drop table dvd_attribute_dvd;

drop table dvd_attribute;

drop table movie;

drop table movie_attribute_movie;

drop table movie_attribute;

drop table setting;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

