# 1. Modulith

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

# 2. Spring Events (Observer Pattern, One Subject multiple Observer)

Spring is Event Driven. Spring itself publishes some events when the application starts like WebServerInitializedEvent
etc.

***We can create custom event and publish from anywhere of our spring application. Then we can listen to that
event from listener. And take actions.***

1. Implement ApplicationEvent to create an Event
2. Implement ApplicationListener or Use @EventListener,@TransactionalEventListener to capture an Event
   `When the task is Transactional we should use @TransactionalEventListener otherwise the event would not be listened to.`
3. Use ApplicationEventPublisher to publish an Event