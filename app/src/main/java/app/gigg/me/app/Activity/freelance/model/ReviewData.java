package app.gigg.me.app.Activity.freelance.model;

import java.util.List;

public class ReviewData {
    public boolean status;
    public Data data;

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

    public class Player{
        public int id;
        public String name;
        public String image_url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }

    public class Review{
        public int id;
        public int reviewer_id;
        public String description;
        public Object uploaded_file;
        public int attitude;
        public int professionalism;
        public int availability;
        public double rating_score;
        public int day_count;
        public Player player;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getReviewer_id() {
            return reviewer_id;
        }

        public void setReviewer_id(int reviewer_id) {
            this.reviewer_id = reviewer_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getUploaded_file() {
            return uploaded_file;
        }

        public void setUploaded_file(Object uploaded_file) {
            this.uploaded_file = uploaded_file;
        }

        public int getAttitude() {
            return attitude;
        }

        public void setAttitude(int attitude) {
            this.attitude = attitude;
        }

        public int getProfessionalism() {
            return professionalism;
        }

        public void setProfessionalism(int professionalism) {
            this.professionalism = professionalism;
        }

        public int getAvailability() {
            return availability;
        }

        public void setAvailability(int availability) {
            this.availability = availability;
        }

        public double getRating_score() {
            return rating_score;
        }

        public void setRating_score(double rating_score) {
            this.rating_score = rating_score;
        }

        public int getDay_count() {
            return day_count;
        }

        public void setDay_count(int day_count) {
            this.day_count = day_count;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }
    }

    public class Capalibity{
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Profile{
        public int id;
        public String school;
        public String date_of_birth;
        public String profile_url;
        public Object country;
        public List<Capalibity> capalibity;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getProfile_url() {
            return profile_url;
        }

        public void setProfile_url(String profile_url) {
            this.profile_url = profile_url;
        }

        public Object getCountry() {
            return country;
        }

        public void setCountry(Object country) {
            this.country = country;
        }

        public List<Capalibity> getCapalibity() {
            return capalibity;
        }

        public void setCapalibity(List<Capalibity> capalibity) {
            this.capalibity = capalibity;
        }
    }

    public class Data{
        public int num_of_reviews;
        public double total;
        public String total_attitude;
        public String total_professionalism;
        public String total_availability;
        public List<Review> reviews;
        public Profile profile;

        public int getNum_of_reviews() {
            return num_of_reviews;
        }

        public void setNum_of_reviews(int num_of_reviews) {
            this.num_of_reviews = num_of_reviews;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getTotal_attitude() {
            return total_attitude;
        }

        public void setTotal_attitude(String total_attitude) {
            this.total_attitude = total_attitude;
        }

        public String getTotal_professionalism() {
            return total_professionalism;
        }

        public void setTotal_professionalism(String total_professionalism) {
            this.total_professionalism = total_professionalism;
        }

        public String getTotal_availability() {
            return total_availability;
        }

        public void setTotal_availability(String total_availability) {
            this.total_availability = total_availability;
        }

        public List<Review> getReviews() {
            return reviews;
        }

        public void setReviews(List<Review> reviews) {
            this.reviews = reviews;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }
    }
}
