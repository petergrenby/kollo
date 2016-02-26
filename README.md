.h1 Project Kollo
Off heap memory allocation with object (JSON structures) serialization/deserialization. It allows direct access to object that are off heap through the immutable Simple Object Structure (or structure).

.h3 Byte Block Buffer
`ByteBlockBufferManager memory = new ByteBlockBufferManager(1024*10);`
`int p = memory.allocate(300);`
`memory.putShort(p, 0, 56);`
`memory.putString(p, 0, "Test");`
`memory.free(p);`

.h2 SOS
Simple Object Structure is a JSON like structure

`ByteBlockBufferManager memory = new ByteBlockBufferManager(1024*10);`
`int blockPointer = SimpleObjectStructure.buildSosByteBlockBuffer(memory, jdm);`
`SosByteBlockBufferMap sos = new SosByteBlockBufferMap(memory, blockPointer);`
