<h1> Project Kollo </h1>
Off heap memory allocation with object (JSON structures) serialization/deserialization. It allows direct access to object that are off heap through the immutable Compact Traversable Object Format.

<h3> Json </h3>
`JsonDataMap jdm = new JsonDataMap();`
`jdm.putByte("by", (byte) 64).putShort("sh", (short) 312).putInt("in", 45).putLong("lo", 76);`

<h3> Allocator </h3>
`ByteBuffer buffer = CtofBuilder.buildCtof(jdm);`
`ByteBlockAllocator allocator = new ByteBlockAllocator(1024*10);`
`int blockPointer = allocator.allocateAndClone(buffer);`

<h3> Ctof </h3>
`CtofDataMap bpo = new CtofDataMap(allocator, blockPointer);`
`bpo.getByteValue("by");`
