package Infra;

public class Constants {

    public static class Connections{
        private static String ip = "192.168.31.244";
        public static String HOST = "http://" + ip +":8080/EyeClass";
        public static String TeacherServlet = HOST +"/teacher";
        public static String LoginServlet = HOST + "/login";
        public static String StudentServlet = HOST + "/student";
    }

    public static class Teacher{
        public static int Demo_lesson_id = 1;
        public static String Demo_class_id = "ORT Eilat_Grade11_1";
        public static int SendTeacherPageMS = 500;
    }

    public static class Student{
        public static int CheckActiveLessonMS = 500;
    }

}
