package app.gigg.me.app.Activity.freelance.model;

import java.util.List;

public class LiveTask {
    public boolean status;
    public List<Task> list;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Task> getList() {
        return list;
    }

    public void setList(List<Task> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "LiveTask{" +
                "status=" + status +
                ", list=" + list +
                '}';
    }

    public class Task{
        public long id;
        public String employee_name;
        public String project_title;
        public String project_description;
        public String target_country;
        public int volume;
        public double amount;
        public String download_link;
        private String update;
        private String freelancer_email;

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "employee_name='" + employee_name + '\'' +
                    ", project_title='" + project_title + '\'' +
                    ", project_description='" + project_description + '\'' +
                    ", target_country='" + target_country + '\'' +
                    ", volume=" + volume +
                    ", amount=" + amount +
                    ", download_link='" + download_link + '\'' +
                    '}';
        }

        public String getFreelancer_email() {
            return freelancer_email;
        }

        public void setFreelancer_email(String freelancer_email) {
            this.freelancer_email = freelancer_email;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getEmployee_name() {
            return employee_name;
        }

        public void setEmployee_name(String employee_name) {
            this.employee_name = employee_name;
        }

        public String getProject_title() {
            return project_title;
        }

        public void setProject_title(String project_title) {
            this.project_title = project_title;
        }

        public String getProject_description() {
            return project_description;
        }

        public void setProject_description(String project_description) {
            this.project_description = project_description;
        }

        public String getTarget_country() {
            return target_country;
        }

        public void setTarget_country(String target_country) {
            this.target_country = target_country;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getDownload_link() {
            return download_link;
        }

        public void setDownload_link(String download_link) {
            this.download_link = download_link;
        }
    }
}
