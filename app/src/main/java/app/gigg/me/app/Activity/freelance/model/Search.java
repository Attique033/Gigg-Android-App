package app.gigg.me.app.Activity.freelance.model;

import java.util.List;

public class Search {
    public boolean status;
    public List<Datum> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum{
        public int id;
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
        public double rating_score;
        public List<Object> capalibity;
        public String profile_image;
        public int age;
        public String country;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public double getRating_score() {
            return rating_score;
        }

        public void setRating_score(double rating_score) {
            this.rating_score = rating_score;
        }

        public List<Object> getCapalibity() {
            return capalibity;
        }

        public void setCapalibity(List<Object> capalibity) {
            this.capalibity = capalibity;
        }
    }
}
