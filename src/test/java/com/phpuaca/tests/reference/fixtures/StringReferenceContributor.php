<?php

namespace Foo
{
    class Bar
    {
        public function getFoobar()
        {
        }
    }
}

namespace PHPUnit\Framework
{
    use PHPUnit\Framework\MockObject\MockBuilder;

    abstract class TestCase
    {
        /**
         * @param $className
         * @return MockBuilder
         */
        public function getMockBuilder($className) {
            return new MockBuilder();
        }
    }
}

namespace PHPUnit\Framework\MockObject
{
    class MockBuilder
    {
        public function setMethods(?array $methods = null): self
        {
            return new self();
        }

        public function addMethods(?array $methods = null): self
        {
            return new self();
        }

        public function onlyMethods(?array $methods = null): self
        {
            return new self();
        }

        public function getMock(): MockObject
        {

        }
    }
}
