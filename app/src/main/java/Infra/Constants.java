package Infra;

public class Constants {

    public static class Connections{

        private static String ip = "192.116.98.70";
        public static String HOST() {return "http://" + ip +":8080/EyeClass";}
        public static String TeacherServlet() {return HOST() +"/teacher";}
        public static String LoginServlet() {return HOST() + "/login";}
        public static String StudentServlet() {return HOST() + "/student";}
        public static String AdminServlet() {return HOST() + "/admin";}
        public static String QuestionsDeliveryServlet() {return HOST() + "/questionsDeliveryServlet";}

        public static void setIP(String _ip){ip = _ip;}
    }

    public static class Permissions{
        public static final int NoPermission = 0;
        public static final int Admin = 1;
        public static final int Teacher = 2;
        public static final int Student = 3;
    }

    public static class Teacher{
        public static String Demo_class_id = "ORT Eilat_Grade11_1";
        public static final String Class_id = "class_id";
    }

    public enum StudentActiveStateNew {
        Unknown,
        Concentrated,
        NotConcentrated;
    }

    public static class Student{
        public static int CheckActiveLessonMS = 1000;
    }

}
