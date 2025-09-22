-- 创建聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    sender VARCHAR(255) NOT NULL COMMENT '发送者邮箱',
    receiver VARCHAR(255) NOT NULL COMMENT '接收者邮箱',
    content TEXT NOT NULL COMMENT '消息内容',
    type VARCHAR(50) NOT NULL DEFAULT 'USER' COMMENT '消息类型：USER-用户消息，SYSTEM-系统消息',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 创建索引以提高查询性能
CREATE INDEX idx_sender_receiver_timestamp ON chat_message(sender, receiver, timestamp DESC);
CREATE INDEX idx_receiver_sender_timestamp ON chat_message(receiver, sender, timestamp DESC);
CREATE INDEX idx_timestamp ON chat_message(timestamp DESC);
