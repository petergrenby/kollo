.h1 Project Kollo
Off heap memory allocation with object (JSON structures) serialization/deserialization. It allows direct access to object that are off heap through the immutable Simple Object Structure (or structure).

.h2 BBB

.h3 Byte Block Buffer

.h3 Byte Block Buffer Manager


.h2 SOS
Simple Object Structure is a JSON like structure

.h2 AVL-Tree

`ByteBlockBufferManager memory = new ByteBlockBufferManager(1024*10);`
`int blockPointer = SosBuilder.buildCtofByteBlockBuffer(memory, jdm);`
`SosByteBlockBufferMap sos = new SosByteBlockBufferMap(memory, blockPointer);`
