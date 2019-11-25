package ru.spbu.mas;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainController {
    private static final int numberOfAgents = 10;

    String[] getNeighbours(int num){
        String [] neighbours= new String[2];
        neighbours[0]=Integer.toString((num+1)%numberOfAgents);
        neighbours[1]=Integer.toString((num-1)%numberOfAgents);

        for (int i=0; i<neighbours.length; i++){
            if (Integer.parseInt(neighbours[i])==0){
                neighbours[i]=Integer.toString(numberOfAgents);
            }
        }

        return neighbours;
    }

    void initAgents(){
        // Retrieve the singleton instance of the JADE Runtime
        Runtime rt = Runtime.instance();
        //Create a container to host the Default Agent
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "10098");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

        try {
            float number=5;
            String [] neighbours= new String[2];
            for(int i=1; i <= MainController.numberOfAgents; i++) {

                Object[] args = new Object[2];
                args[0] = number;
                args[1] = getNeighbours(i);

                AgentController agent = cc.createNewAgent(Integer.toString(i), "ru.spbu.mas.DefaultAgent", args);
                agent.start();

                number+=3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
