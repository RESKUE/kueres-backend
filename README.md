# KUERES Backend Framework


## Änderungen am Entwurf

#### Event System
> Problem: Hibernate kann keine Objects oder generische Klassen serialisieren
> Lösung: Einschränkung von Objects an Events auf BaseEntities. Nur speichern von entityId + entityType
- EventEntity
entity: Object -> entityType: Class<? extends BaseEntity<?>>, entityId: long + getters/setters
- EventService
receiveMessage(messageObject: Message, message: String, senderIdentifier: String)
->
receiveMessage(entity: BaseEntity<?>, senderIdentifier: String, message: String, type: int)
- BaseService
sendEvent(object: Object) REMOVED

#### Konfiguration
> Problem: Spring kann keine finalen oder static Variablen injecten: topicExchange/defaultQueue config
> Lösung: Entfernen der topicExchange/defaultQueue config Optionen:
TOPIC_EXCHANGE="kueres-events"
DEFAULT_QUEUE="event-consumer"
