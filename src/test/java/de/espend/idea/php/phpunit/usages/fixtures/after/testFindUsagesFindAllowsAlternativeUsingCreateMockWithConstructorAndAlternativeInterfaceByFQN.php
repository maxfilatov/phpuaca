<?php

declare(strict_types=1);

namespace MockeryPlugin\DemoProject;

use Mockery;
use Mockery\Adapter\Phpunit\MockeryPHPUnitIntegration;
use Mockery\MockInterface;
use phpDocumentor\Reflection\Types\Boolean;
use PHPUnit\Framework\TestCase;

interface AlternativeInterface
{
    public function alternative<caret>CalledMethod(string $parameter): string;

    public function alternativeSecondCalledMethod(string $parameter): string;

    public function alternativeUncalledMethod(): void;

    public function alternativeSecondUncalledMethod(): void;
}

class MainClassWithMockeryTest extends TestCase
{
    use MockeryPHPUnitIntegration;

    private MainClass $underTest;

    /** @var Dependency|MockInterface */
    private $dependency;

    protected function setUp(): void
    {
        parent::setUp();
        $this->dependency = Mockery::mock('MockeryPlugin\DemoProject\DependencyWithConstructor', 'MockeryPlugin\DemoProject\AlternativeInterface', ['suffix']);
        $this->underTest = new MainClass($this->dependency);
    }

    public function testInvokeWithAllows(): void
    {
        $this->dependency->allows('<usage>')->andReturns('mocked result');
        $result = $this->underTest->invokeMethodOfDependency('test parameter');
        self::assertSame('mocked result', $result);
    }
}