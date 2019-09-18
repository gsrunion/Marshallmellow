# Marshallmellow
Annotation based solution to marshalling/unmarshalling Pojos to binary representations for interop with other languages

# Problem 
Java provides few mechansims for handling structured binary data sent over the network or through some peripheral interface. ByteBuffer (https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html) serves as the basis for almost all binary IO in the Java ecosystem. While it can be used easily enough to convert byte array data to Java primitives it lacks support for handling compound objects or support for handling unsigned integer types as pouplated by languages that have unsigned types. For the latter the Netty Projects ByteBuff (https://netty.io/4.0/api/io/netty/buffer/ByteBuf.html) handles this well but has no support for handling compound objects.
