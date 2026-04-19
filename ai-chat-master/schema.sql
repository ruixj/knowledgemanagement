create table sys_chat_conversation(
    id int(11) not null auto_increment primary key  ,
    name varchar(255) not null ,
    user_id int(11) not null ,
    create_time datetime not null ,
    update_time datetime not null ,
    is_deleted int(11) not null ,
    create_by int(11) not null ,
    update_by int(11) not null ,
);

create table sys_chat_messages(
    id int(11) not null auto_increment primary key  ,
    conversation_id int(11) not null ,

    content varchar(255) not null ,
    create_time datetime not null ,
    update_time datetime not null ,
    is_deleted int(11) not null ,
    create_by int(11) not null ,
    update_by int(11) not null ,
);