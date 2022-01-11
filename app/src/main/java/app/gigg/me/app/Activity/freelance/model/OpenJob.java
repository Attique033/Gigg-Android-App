package app.gigg.me.app.Activity.freelance.model;

import java.util.List;

public class OpenJob {
    public int id;
    public String email;
    public String project_title;
    public Object project_file;
    public Object type;
    public double amount;
    public List<Submitter> submitters;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProject_title() {
        return project_title;
    }

    public void setProject_title(String project_title) {
        this.project_title = project_title;
    }

    public Object getProject_file() {
        return project_file;
    }

    public void setProject_file(Object project_file) {
        this.project_file = project_file;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<Submitter> getSubmitters() {
        return submitters;
    }

    public void setSubmitters(List<Submitter> submitters) {
        this.submitters = submitters;
    }

    public class Submitter{
        public int id;
        public int freelance_id;
        public int player_id;
        public int freelancer_id;
        public String download_link;
        public Player player;
        public String freelance_name;

        public String getFreelance_name() {
            return freelance_name;
        }

        public void setFreelance_name(String freelance_name) {
            this.freelance_name = freelance_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFreelance_id() {
            return freelance_id;
        }

        public void setFreelance_id(int freelance_id) {
            this.freelance_id = freelance_id;
        }

        public int getPlayer_id() {
            return player_id;
        }

        public void setPlayer_id(int player_id) {
            this.player_id = player_id;
        }

        public int getFreelancer_id() {
            return freelancer_id;
        }

        public void setFreelancer_id(int freelancer_id) {
            this.freelancer_id = freelancer_id;
        }

        public String getDownload_link() {
            return download_link;
        }

        public void setDownload_link(String download_link) {
            this.download_link = download_link;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }
    }


    public class Player{
        public int id;
        public String email;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
