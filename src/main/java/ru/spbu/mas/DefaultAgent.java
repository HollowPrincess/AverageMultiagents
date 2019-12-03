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
    private double number;
    private int senderName;

    protected String[] getLinkedAgents(){
        return linkedAgents;
    }
    protected double getNumber(){
        return number;
    }
    protected void changeNumber(double difference){
        this.number+=difference;
    }
    protected int getSender(){
        return senderName;
    }
    protected void changeSender(int newSenderName){
        this.senderName=newSenderName;
    }


    @Override
    protected void setup(){
        int id = Integer.parseInt(getAID().getLocalName());
        senderName = id;
        Object[] myArgs=getArguments();
        number=(float) myArgs[0];

        linkedAgents=(String []) myArgs[1];

        System.out.println("Agent #" + id +" number: "+ number);
        addBehaviour(new FindAverage(this, TimeUnit.SECONDS.toMillis(1)));
    }
}
