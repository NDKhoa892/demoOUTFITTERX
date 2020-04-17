package com.harry.demooutfitterx.User;

import java.io.Serializable;

public class PostAndFollow implements Serializable {
    long post, follower, following;

    public PostAndFollow() {
    }

    public PostAndFollow(long post, long follower, long following) {
        this.post = post;
        this.follower = follower;
        this.following = following;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }

    public long getFollower() {
        return follower;
    }

    public void setFollower(long follower) {
        this.follower = follower;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }
}
