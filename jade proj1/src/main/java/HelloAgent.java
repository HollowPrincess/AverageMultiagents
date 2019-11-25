import jade.core.Agent;

public class HelloAgent extends Agent
{
    protected void setup()
    {
        System.out.println("Hello World. ");
        System.out.println("My name is "+ getLocalName());
    }
}
