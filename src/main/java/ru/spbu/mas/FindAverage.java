package ru.spbu.mas;

import jade.core.AID;
import jade.core.Runtime;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Collections;
import java.util.HashMap;

public class FindAverage extends TickerBehaviour {
    private final DefaultAgent agent;
    private int currentStep;
    private final int MAX_STEPS = 15;


    FindAverage(DefaultAgent agent, long period) {
        super(agent, period);
        this.setFixedPeriod(true);
        this.agent = agent;
        this.currentStep = 0;
    }

    protected void OutResult(){
        //System.out.println("comparison!");

        if(Integer.parseInt(this.agent.getLocalName())==(Collections.min(this.agent.infoAboutAgents.keySet()))){
            System.out.println("Average is: " + this.agent.infoAboutAgents.values().stream().mapToDouble(i->i).sum()/this.agent.infoAboutAgents.size()+" get by "+this.agent.getLocalName());

            ACLMessage new_msg = new ACLMessage(ACLMessage.INFORM);
            new_msg.setContent("stop!");
            for (int i = 0; i < this.agent.getLinkedAgents().length; i++) {
                new_msg.addReceiver(new AID(this.agent.getLinkedAgents()[i], AID.ISLOCALNAME));
            }
            this.agent.send(new_msg);
            //System.out.println(this.agent.getLinkedAgents().length+" msgs sent"+this.agent.getLocalName());
            long usedBytes = java.lang.Runtime.getRuntime().totalMemory()-java.lang.Runtime.getRuntime().freeMemory();
            System.out.println("used bytes: "+usedBytes);
        }
        this.stop();
    }


    @Override
    protected void onTick() {
        if (currentStep < MAX_STEPS) {
            HashMap<Integer, Float> newsAboutAgents = new HashMap<>();
            //listen to all neighbours
            for (int ag = 0; ag < this.agent.getLinkedAgents().length; ag++) {
                ACLMessage msg = agent.receive();
                if ((msg != null) & (msg.getContent()!="stop!")){
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, Float> receivedInfo = (HashMap<Integer, Float>) Utils.deserializeFromString(msg.getContent());
                    receivedInfo.keySet().removeAll(this.agent.infoAboutAgents.keySet());
                    //System.out.println("remove with comparison!");
                    newsAboutAgents.putAll(receivedInfo);
                } else if (msg.getContent()!="stop!"){
                    block();
                }else {
                    OutResult();
                }
            }
            if (newsAboutAgents.size() == 0) {//if all news are old then stop
                OutResult();
            } else {//if we have new info we should save and send it
                this.agent.infoAboutAgents.putAll(newsAboutAgents);

                ACLMessage new_msg = new ACLMessage(ACLMessage.INFORM);
                new_msg.setContent(Utils.serializeToString(newsAboutAgents));
                for (int i = 0; i < this.agent.getLinkedAgents().length; i++) {
                    new_msg.addReceiver(new AID(this.agent.getLinkedAgents()[i], AID.ISLOCALNAME));
                }
                //System.out.println(this.agent.getLinkedAgents().length+" msgs sent"+this.agent.getLocalName());

                this.agent.send(new_msg);
            }
            System.out.println("Agent " + this.agent.getLocalName() + ": tick=" + getTickCount());
            this.currentStep++;
        } else {
            this.stop();
        }
    }
}
