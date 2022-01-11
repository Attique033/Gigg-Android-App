package app.gigg.me.app.Activity.freelance.model;

import java.util.List;

public class ProfileSetting {
    public boolean status;
    public Data data;
    public Youtube youtube;
    public Twitter twitter;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Youtube getYoutube() {
        return youtube;
    }

    public void setYoutube(Youtube youtube) {
        this.youtube = youtube;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public class Twitter {
        public int followers;
        public int tweets;

        public int getFollowers() {
            return followers;
        }

        public void setFollowers(int followers) {
            this.followers = followers;
        }

        public int getTweets() {
            return tweets;
        }

        public void setTweets(int tweets) {
            this.tweets = tweets;
        }
    }

    public class Capalibity {
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Data {
        public String username;
        public String occupation;
        public String about;
        public String school;
        public String date_of_birth;
        public Object facebook_token;
        public String twitter_token;
        public String instagram_token;
        public String youtube_channel;
        public String created_at;
        public String updated_at;
        public String profile_url;
        public int age;
        public List<Capalibity> capalibity;
        public String country;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getDate_of_birth() {
            return date_of_birth;
        }

        public void setDate_of_birth(String date_of_birth) {
            this.date_of_birth = date_of_birth;
        }

        public Object getFacebook_token() {
            return facebook_token;
        }

        public void setFacebook_token(Object facebook_token) {
            this.facebook_token = facebook_token;
        }

        public String getTwitter_token() {
            return twitter_token;
        }

        public void setTwitter_token(String twitter_token) {
            this.twitter_token = twitter_token;
        }

        public String getInstagram_token() {
            return instagram_token;
        }

        public void setInstagram_token(String instagram_token) {
            this.instagram_token = instagram_token;
        }

        public String getYoutube_channel() {
            return youtube_channel;
        }

        public void setYoutube_channel(String youtube_channel) {
            this.youtube_channel = youtube_channel;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getProfile_url() {
            return profile_url;
        }

        public void setProfile_url(String profile_url) {
            this.profile_url = profile_url;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public List<Capalibity> getCapalibity() {
            return capalibity;
        }

        public void setCapalibity(List<Capalibity> capalibity) {
            this.capalibity = capalibity;
        }
    }

    public class Youtube {
        public String subscriber;
        public String views;

        public String getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(String subscriber) {
            this.subscriber = subscriber;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }
    }
}
