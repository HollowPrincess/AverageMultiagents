package ru.spbu.mas;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.LinkedList;

public class FindAverage extends TickerBehaviour {
    private final DefaultAgent agent;
    private int currentStep = 0;
    private int stopCounter = 0;

    private int delayedStep = -5;
    private ACLMessage delayedMsg;
    private final int MAX_STEPS = 1000;

    private int comparisonCounter = 0;
    private int sumCounter = 0;
    private int divisionCounter = 0;
    private int multiCounter = 0;
    private int msgsCounter = 0;


    FindAverage(DefaultAgent agent, long period) {
        super(agent, period);
        this.setFixedPeriod(true);
        this.agent = agent;
    }

    protected void OutResult(){
        this.comparisonCounter++;
        if(Integer.parseInt(this.agent.getLocalName())==(this.agent.getSender())) {
            System.out.println("Average is: " + this.agent.getNumber() + " get by " + this.agent.getLocalName());
            sendStop();
            System.out.println(" Steps num:" + this.currentStep);
        }

        System.out.println(
                "agent: "+ this.agent.getAID().getLocalName()+
                        " comparisons: " + this.comparisonCounter+
                        " sums: " + this.sumCounter +
                        " divisions: " + this.divisionCounter +
                        " multiplications: " + this.multiCounter +
                        " msgs: " + this.msgsCounter
        );

        this.stop();
    }

    protected void sendStop(){
        ACLMessage new_msg = new ACLMessage(ACLMessage.INFORM);
        new_msg.setContent("stop!");
        for (int i = 0; i < this.agent.getLinkedAgents().length; i++) {
            new_msg.addReceiver(new AID(this.agent.getLinkedAgents()[i], AID.ISLOCALNAME));
            this.msgsCounter++;
        }

        this.agent.send(new_msg);
    }

    protected void sendNumber(){
        if (this.delayedStep+1==this.currentStep){
            //if we have delayed on previous step msg then send it
            this.agent.send(this.delayedMsg);
            this.msgsCounter++;
        }
        double noise = Math.random()*5-2.5;

        ACLMessage msgSendNum = new ACLMessage(ACLMessage.INFORM);
        String[] message = new String[2];
        message[0] = Double.toString(this.agent.getNumber()+noise);
        message[1] = Integer.toString(this.agent.getSender());
        msgSendNum.setContent(Utils.serializeToString(message));

        for (String linkedAgent : this.agent.getLinkedAgents()) {
            msgSendNum.addReceiver(new AID(linkedAgent, AID.ISLOCALNAME));
            this.msgsCounter++;
            if (
                    !(
                    (this.agent.getAID().getLocalName().equals("3")) &&
                            (linkedAgent.equals("4")) &&
                            (Math.random()>=0.5)
                    //if not (it is the temporary edge (3->4) which is not exist with 1/2 probability)
                    )
            ){
                if (
                        (this.agent.getAID().getLocalName().equals("5")) &&
                        (linkedAgent.equals("6")) &&
                        (this.delayedStep+1!=this.currentStep) &&
                        (Math.random()< 1.0/3.0)
                    // if it is 5->6 edge and it wasn't delayed on previous step
                    // then it will be delayed with 1/3 probability
                ){
                    //msg to delay:
                    this.delayedStep = this.currentStep;
                    this.delayedMsg = new ACLMessage(ACLMessage.INFORM);
                    this.delayedMsg.setContent(Utils.serializeToString(message));
                    this.delayedMsg.addReceiver(new AID(linkedAgent, AID.ISLOCALNAME));

                } else {
                    msgSendNum.addReceiver(new AID(linkedAgent, AID.ISLOCALNAME));
                    this.msgsCounter++;
                }
            }
        }
        this.agent.send(msgSendNum);
    }


    @Override
    protected void onTick() {
        if (currentStep < MAX_STEPS) {
            double difference=0;
            ArrayList <String> senders = new ArrayList<>();

            //send the Number
            sendNumber();
            //listen others numbers
            int queueSize = this.agent.getCurQueueSize();
            LinkedList<ACLMessage> futureInfo = new LinkedList<>();// this list for msgs which sender send more than one msg
            String [] receivedInfo;

            for (int i=0; i<queueSize; i++){
                ACLMessage msg = agent.receive();

                this.comparisonCounter++;
                if ((msg != null) &&(!senders.contains(msg.getSender().getLocalName()))) {
                    senders.add(msg.getSender().getLocalName());

                    if (!msg.getContent().equals("stop!")) {
                        receivedInfo = (String[]) Utils.deserializeFromString(msg.getContent());

                        difference += (Double.parseDouble(receivedInfo[0]) - this.agent.getNumber());//u(sum)
                        this.sumCounter++;

                        if (Integer.parseInt(receivedInfo[1]) < this.agent.getSender()) {
                            this.agent.changeSender(Integer.parseInt(receivedInfo[1]));
                        }
                    } else {
                        sendStop();
                        this.stop();
                    }
                } else if (msg != null){
                    futureInfo.addFirst(msg);
                    // if we have info from this sender then we should get this info in future
                    // if we have more than 1 msgs from one sender than break this loop
                    break;
                }
            }
            //change state
            this.agent.changeNumber(0.1*difference);
            this.multiCounter++;
            //get future msgs to the queue:
            for(ACLMessage msg: futureInfo) {
                agent.putBack(msg);
            }

            //System.out.println("Agent " + this.agent.getLocalName() + ": tick=" + getTickCount());
            this.currentStep++;
        } else {
            //System.out.println("Agent " + this.agent.getLocalName() + ": reach steps num");
            OutResult();
        }
    }
}
