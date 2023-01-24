### 前言

**IoC 容器**具有依赖注入功能的容器，它可以创建对象，IoC 容器负责实例化、定位、配置应用程序中的对象及建立这些对象间的依赖。通常new一个实例，控制权由程序员控制，而"控制反转"是指new实例工作不由程序员来做而是交给Spring容器来做，实现了组件之间的解耦，提高了程序的灵活性和可维护性。

![img](https://raw.githubusercontent.com/congee02/pics/origin/202301241758395.png)

### 基于注解

```
Java 的反射机制允许访问一个类上的注解，以及类中属性的注解，再取得注解中的属性，可用于构建 Ioc。
```

```java
Class clazz = this.getClass();
SampleAnnotation annotation = clazz.isAnnotationPresent(SampleAnnotation.class) ? 
    							clazz.getAnnotation(SampleAnnotation.class) : null;
if (annotation == null) {
    // do something ...
} else {
    // do something else 
}
```

### 	IoC 注解

1. @ComponentScan

   ```java
   /** 确定需要组件扫描的包
    * @author congee(congee02 @ 163.com)
    * @date 1/24/2023 3:25 PM
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   public @interface ComponentScan {
   
       /**
        * @return 需要组件扫描的包
        */
       String value() default "";
   
   }
   ```

2. @Component

   ```java
   /** 声明类为 bean
    * @author congee(congee02 @ 163.com)
    * @date 1/24/2023 3:15 PM
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   public @interface Component {
   
       /**
        * @return bean 的名称
        */
       String value() default "";
   
   }
   ```

3. @Scope

   ```java
   /** 作用域
    * @author congee(congee02 @ 163.com)
    * @date 1/24/2023 4:17 PM
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   public @interface Scope {
       /**
        * @return 作用域
        */
       ScopeEnum value() default ScopeEnum.SINGLETON;
   }
   ```

4. @Autowired

   ```java
   /** 标志自动装配
    * @author congee(congee02 @ 163.com)
    * @date 1/24/2023 5:02 PM
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   public @interface Autowired {
   }
   ```



### Bean 的定义

```
用 类型 和 作用域 定义一个 bean
```

```java
/** bean 的定义
 * @author congee(congee02 @ 163.com)
 * @date 1/24/2023 3:18 PM
 */
public class BeanDefinition {

    /**
     * bean 的类型
     */
    private Class type;

    /**
     * bean 的作用域 {@link ScopeEnum}
     */
    private ScopeEnum scope;


    public Class<?> getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        this.scope = scope;
    }
}
```

### IoC 容器

1. IoC 如何存储 bean

   ```
   使用 ConcurrentHashMap 存储。其中一个存储所有 bean 的 BeanDefinition，另一个特殊存储单例 bean
   ```

   ```java
   /**
    * 保证 singleton bean 的引用只指向一个对象，用于特殊处理 singleton bean 的 map
    */
   private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
   
   /**
    * IoC 容器使用 map 存储
    */
   private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
   ```

   

2. IoC 如何存放 bean

   ```
   1. 传入带 @ComponentScan 的配置类
   	从 ComponentScan 的注解中取得要组件扫描的包名
   
   
   2. 遍历上述包下所有类的 .java 文件对应的 .class 文件
   
   	若 .class 文件对应的类上有 Component 注解，则判定为 bean，获取其类型对象 clazz，否则忽略。
   	
   	判定为 bean 后，从注解中取得 bean 的名称 beanName，若为空字符串，则使用默认的命名规则 ,BeanDefinition definition = new BeanDefinition(); 检查作用域，若类上无 Scope 注解 或 有 Scope 注解但无值，则设置该 bean 的作用域为 单例 (SINGLETON) (definition.setScope(SINGLETON))，否则作用域设为 Scope 的值。definition 中，类型为 clazz，clazz.setType(clazz)。最后将 definition 置入 beanDefinitionMap，其 key 为 beanName。
   	
   	在所有 bean 都被实例化后，需要特殊处理 单例 bean，将其置入专门存储单例 bean 的 singletonObjects 中。
   	
   	考虑自动装配 @Autowired，使用反射机制，寻找所有带 @Autowired 的属性。若某属性 Field f 带有 @Autowired 注解，在 IoC 容器中寻找合适的 bean，后使用 f.setAccessible(true) 和 f.set(instance, bean) 实现注入。
   ```

   

3. 如取出 bean

   ```
   IoC 容器通过 getBean() 方法接收 bean 的名称，其内部先从 beanDefinitionMap 取出 BeanDefinition definition，若 definition 描述该 bean 为单例，从 singletonObjects 中取，而非再次创建，保证多次取出的 bean 是同一个。若为多例，则新创建一个对象返回。
   ```



