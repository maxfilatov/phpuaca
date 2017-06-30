<?php

namespace Bar
{
    class Foo
    {
    }
    class Car
    {
    }
}

namespace Foo
{
    use Bar\Car;
    use Bar\Foo;

    class Bar
    {
        public function __construct(Foo $foo, Car $car)
        {
        }
    }

    class BarNext
    {
        public function __construct(Foo $foo, Car $car, Car $car2, Car $car3)
        {
        }
    }
}

namespace PHPUnit\Framework
{
    class TestCase
    {
    }
}
