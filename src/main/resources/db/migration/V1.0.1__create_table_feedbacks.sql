CREATE TABLE IF NOT EXISTS feedbacks_tb (
    feedback_id UUID PRIMARY KEY,
    sender_user_id UUID NOT NULL,
    receiver_user_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT feedbacks_users_sender FOREIGN KEY (sender_user_id) REFERENCES users_tb(user_id),
    CONSTRAINT feedbacks_users_receiver FOREIGN KEY (receiver_user_id) REFERENCES users_tb(user_id)
);