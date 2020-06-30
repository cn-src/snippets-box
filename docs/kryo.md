# Kryo 序列化

* 支持 eclipse-collections 的集合类型的序列化/反序列化

* Kryo [池化封装](https://github.com/EsotericSoftware/kryo#pooling)
```java
class Demo {
    KryoHelper kryoHelper = new KryoHelper(kryo -> {
        kryo.register(User.class);
    });

    void demo() {
        byte[] bytes = kryoHelper.writeClassAndObject(user);
        User user = kryoHelper.readClassAndObject(bytes);
    }
}
```  