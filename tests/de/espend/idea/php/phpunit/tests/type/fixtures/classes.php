<?php

namespace PHPUnit\Framework
{
    class TestCase
    {
        static function createMock() {}
        static function prophecy() {}
    };
}

namespace
{
    class Foo
    {
        public function bar() {}
    }
}
