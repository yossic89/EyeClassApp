package Infra;

public class Constants {

    public static class Connections{
        private static String ip = "192.168.205.144";
        public static String HOST = "http://" + ip +":8080/EyeClass";
        public static String TeacherServlet = HOST +"/teacher";
        public static String LoginServlet = HOST + "/login";
        public static String StudentServlet = HOST + "/student";
    }

    public static class Permissions{
        public static final int NoPermission = 0;
        public static final int Admin = 1;
        public static final int Teacher = 2;
        public static final int Student = 3;
    }

    public static class Teacher{
        public static int Demo_lesson_id = 1;
        public static String Demo_class_id = "ORT Eilat_Grade11_1";
        public static int SendTeacherPageMS = 500;
        public static final String Class_id = "class_id";
    }

    public static class Student{
        public static int CheckActiveLessonMS = 1000;
    }

}
