<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryTestCase;
use Mockery\MockInterface;

class MainClassWithMockeryTest extends MockeryTestCase
{
    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        parent::setUp();
        $this->dependency = Mockery::mock(Dependency::class);
    }

    public function testInvokeWithShouldReceive(): void
    {
        $this->dependency->should<caret>Receive('calledMethod', 'secondCalledMethod')->twice()->andReturn('result');
    }
}