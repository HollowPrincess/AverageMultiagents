package ru.spbu.mas;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DefaultAgent extends Agent{
    private String[] linkedAgents;
    private float number;

    protected HashMap<Integer, Float> infoAboutAgents = new HashMap<>();

    protected String[] getLinkedAgents(){
        return linkedAgents;
    }
    protected float getNumber(){
        return number;
    }


    @Override
    protected void setup(){
        int id = Integer.parseInt(getAID().getLocalName());
        Object[] myArgs=getArguments();
        number=(float) myArgs[0];
        infoAboutAgents.put(id, number);

        linkedAgents=(String []) myArgs[1];

        System.out.println("Agent #" + id +" number: "+ number);
        addBehaviour(new FindAverage(this, TimeUnit.SECONDS.toMillis(1)));

        // send a message to all linked agents
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(Utils.serializeToString(infoAboutAgents));
        for (int i=0; i<linkedAgents.length;i++){
            msg.addReceiver(new AID(linkedAgents[i], AID.ISLOCALNAME));
        }
        //System.out.println("msg sent");
        send(msg);
    }
}
