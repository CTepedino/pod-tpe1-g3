package ar.edu.itba.pod.client;

public class administrationClient {
    public static void main(String[] args){
        String address = System.getProperty("serverAddress");
        if (address == null){
            System.out.println("No address specified");
            return;
        }
        //TODO: Connect to server
        String action = System.getProperty("action");
        if (action == null){
            System.out.println("No action specified");
            return;
        }
        switch(action){
            case "addRoom":
                addRoom();
                break;
            case "addDoctor":
                addDoctor();
                break;
            case "setDoctor":
                setDoctor();
                break;
            case "checkDoctor":
                checkDoctor();
                break;
            default:
                System.out.println("Unrecognized action: " + action);
        }
    }

    private static void addRoom(){
        int roomNumber = 0;
        System.out.printf("Room #%d added successfully", roomNumber);
    }

    private static void addDoctor(){
        String doctor = System.getProperty("doctor");
        int level = Integer.parseInt(System.getProperty("level"));

        System.out.printf("Doctor %s (%d) added successfully", doctor, level);
    }

    private static void setDoctor(){

    }

    private static void checkDoctor(){

    }
}
