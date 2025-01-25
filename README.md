# 1. Modulith

Dependencies

1. spring-modulith-events-jpa (for saving events)
2. spring-modulith-actuator (for /actuator/modulith but requires spring-boot-starter-actuator)
3. spring-modulith-starter-jpa (if jpa is used in project. If you add this there is no need of
   spring-modulith-starter-core)
4. spring-modulith-observability

Under the root package `xyz.sadiulhakim` all the packages are called module in Spring Modulith.
And files directly under modules `not under any nested package` are called public api like: AppConfig
under config module is called public Api. Any `public` class under any package under any module can not be accessed from
other module by default
`package private, private, protected file are not accessable outside as they are still java types`.

## Ways to make them available outside

### Testing - This testing makes sure you do not violate modularity rules.

Crate a test file then declare a ApplicationModules instance like this
`static ApplicationModules modules = ApplicationModules.of(Application.class);` then
inside a test method call `modules.verify()`.

1. Named Interface
   `Create package-info.java file under sub packages and use @NamedInterface annotation to give a name or just use @NamedAnnotation on the type(class,record) name. NamedInterface make type(class,record) avilable outside`.
2. Open Application Modules
   `Create package-info.java file under module and use @ApplicationModule annotation to select type Open, but that makes all the files avilable outside of the module`
3. allowedDependencies
   `this paramater is used inside @ApplicationModule to specify which modules are allowed to be this modules dependency. After the = sign use {} to pass command seprated modules name and use {moduleName} :: {NameFilterface name} this syntax to pass alowed NamedInterfaces name.`

### Modulith Events

In Spring Modulith, instead of making circular dependency we can make use of spring events to do dependent tasks. Like
you have to delete a UserRole by id,
but before deleting first you have to make sure any user does not have this role. If any user has this role you should
not delete this. In this scenario, we need to inject UserService
into RoleService but that is what would make Spring Moldulith Angry. How to solve this? When delete method is called
publish an event with role id and do not inject UserService in
RoleService. Then in a different EventListener class inject Role And User Service. For listening to an event you can use
`EventListsner or TransactionalEventListener` Annotation.
You can use `@Async` annotation to make listening Asynchronous.

***But remember for `@TransactionalEventListener` the
publishing method or class should be annotated with `@Transactional`.
Same to `@ApplicationModuleListener` as it is annotated with
`@TransactionalEventListener , @Async and @Transactional(propagation = Propagation.REQUIRES_NE)`***.

#### Example of a dependency cycle

Suppose we have modules like this

```
user - module
   web (package)
      UserController (class)
   model (package)
      UserService (class)

role - module
   RoleEventListener (class)
   model (package)
      RoleService (class)
   web (package)
      RoleController
```

UserController has dependency of RoleService `which is public api (directly under module)` which belongs to role module
and RoleEventListener `which is a public api` has dependency of UserService which belongs to user module.
Though dependent classes are public api still there is a cycle. It is all about module, does not matter if the class is
a public api or not.

# 2. Spring Events (Observer Pattern, One Subject multiple Observer)

Spring is Event Driven. Spring itself publishes some events when the application starts like WebServerInitializedEvent
etc.

***We can create custom event and publish from anywhere of our spring application. Then we can listen to that
event from listener. And take actions.***

1. Implement ApplicationEvent to create an Event
2. Implement ApplicationListener or Use @EventListener,@TransactionalEventListener to capture an Event
   `When the task is Transactional we should use @TransactionalEventListener otherwise the event would not be listened to.`
3. Use ApplicationEventPublisher to publish an Event

# 3. Spring Session with Redis

Dependency

1. spring-boot-starter-data-redis
2. spring-session-data-redis

In Java, use  `HttpSession` to access and Send session.
In Thymeleaf use `${session}` to access session.

# 4. Spring Integration

Dependency
1. spring-integration-jpa

Spring Integration is an extension of the Spring framework that provides a lightweight and flexible solution for
building
enterprise integration solutions. It supports messaging-based architectures to connect different systems, applications,
or processes within an enterprise. Using well-defined patterns, it enables seamless communication and data exchange
between
components while ensuring loose coupling and scalability.

## Key Elements of Spring Integration

1. `Endpoints` are components that enable interaction between external systems and the integration framework. Examples
   include inbound adapters (consume data from external sources) and outbound adapters (send data to external systems).
2. `Filters` determine whether a message should be allowed to proceed through the integration flow. They act as
   decision-makers, accepting or rejecting messages based on specified criteria.
3. `Transformers` modify or convert the content of a message to a required format. For example, they can convert a JSON
   payload into a Java object or vice versa.
4. `Routers` determine the path a message should take based on its content or metadata. For example, a router can direct
   messages to different endpoints based on a header value.
5. `Service activators` are endpoints that connect a Spring service (business logic) to the messaging system. They
   process
   incoming messages and produce responses, if necessary.
6. `Channels` are the conduits for passing messages between different components in Spring Integration. They decouple
   the
   sender and receiver to ensure flexibility and scalability.

## Types of Channels in Spring Integration

1. `Direct Channel` - Messages are sent directly from the sender to the receiver within the same thread. This is
   synchronous and has low overhead.
2. `Queue Channel` - Acts like a message queue where messages are stored until consumed by a receiver. It supports
   asynchronous communication.
3. `Publish-Subscribe Channel` - Allows multiple consumers to subscribe to the same channel and receive copies of the
   same message, enabling broadcast messaging.
4. `Executor Channel` - Utilizes a task executor for asynchronous message handling. It decouples the sender and receiver
   threads for better performance.
5. `Priority Channel` - Processes messages based on priority rather than arrival order, enabling prioritized message
   handling.

## Integration Flow

An integration flow defines the sequence of components through which messages pass in a Spring Integration application.
It typically consists of:

1. Message Source – Generates or receives messages.
2. Channels – Pass messages between components.
3. Message Processors – Includes filters, transformers, routers, and service activators.
4. Message Destination – The endpoint where messages are sent or processed.

## How Integration Flow Works

1. A Message Source (e.g., an inbound adapter) receives data from an external system or generates it.
2. The message is passed into a Channel.
3. A Filter may determine if the message should proceed.
4. If accepted, a Transformer converts the message into the desired format.
5. A Router can direct the message to appropriate components or endpoints.
6. The Service Activator processes the message or performs business logic.
7. The message is delivered to its final Message Destination, which could be another system, database, or file.