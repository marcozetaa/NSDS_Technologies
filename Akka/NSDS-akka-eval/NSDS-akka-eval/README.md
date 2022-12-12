# Evaluation lab - Akka

## Group number: 11

## Group members

- Marco Zanghieri
- Francesco Scandale 
- Luca Rondini 

## Description of message flows

Assumption: WORKERs could be under the same class, but we kept them separate to easily distinguish their prints and roles.<br /> 

SUBSCRIBER uses _SubscribeMsg_ to tell the BROKER it wants to subscribe to a topic.<br />
BROKER forwards _SubscribeMsg_ to a WORKER, based on the message's key (odd or even). WORKER saves the subscription and the SUBSCRIBER.<br />
PUBLISHER sends a _PublishMsg_ to the BROKER for a certain Topic. <br />
BROKER forwards _PublishMsg_ to both WORKERs.<br />
- WORKER which holds the subscription for that topic, sends a _NotifyMsg_ to the corresponding SUBSCRIBER. <br />
- WORKER not holding the subscription throws an Exception managed by the SUPERVISOR<br />

**isOn Scenario**<br />
BROKER receives a _Batchmsg_ <br />
BROKER starts stashing _PublishMsg_<br />

BROKER receives a _BatchMsg_ <br />
BROKER unstash all _PublishMsg_ stored and forwards them to the WORKERs as the previous scenario<br />