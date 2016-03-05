# Project Kollo
Off heap memory allocation with object (JSON structures) serialization/deserialization. It allows direct access to object that are off heap through the immutable Simple Object Structure (or structure).

## Byte Block Buffer
Byte Block Buffer (BBB) is a, off heap, block based memory area. It keeps block and can split and merge block.

````java
ByteBlockBufferManager memory = new ByteBlockBufferManager(1024*10);
int p = memory.allocate(300);
memory.putShort(p, 0, 56);
memory.putString(p, 0, "Test");
memory.free(p);
````

## Simple Object Structure
Simple Object Structure (SOS) is a JSON like structure

````java
ByteBlockBufferManager memory = new ByteBlockBufferManager(1024*10);
int blockPointer = SimpleObjectStructure.buildSosByteBlockBuffer(memory, jdm);
SosByteBlockBufferMap sos = new SosByteBlockBufferMap(memory, blockPointer);
````
