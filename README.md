PHPUnit Autocomplete Assistant
==============================
PhpStorm plugin to provide smart autocompletion for mocked class methods and properties.

Supported versions of PhpStorm: 7.1, 8

Releases
--------
* Stable [phpuaca.jar](https://github.com/maxfilatov/phpuaca/raw/master/phpuaca.jar)
* Latest unstable [phpuaca.jar](https://github.com/maxfilatov/phpuaca/raw/unstable/phpuaca.jar)

Installation
------------
* Go to `PhpStorm -> Preferences... -> Plugins -> Install plugin from disk...` and choose `phpuaca.jar`
* Restart PhpStorm

Demo
----
* `PHPUnit_Framework_MockObject_MockBuilder::setMethods`
![PHPUnit_Framework_MockObject_MockBuilder::setMethods](https://raw.githubusercontent.com/maxfilatov/phpuaca/master/img/mock_builder_set_methods.png)

* `PHPUnit_Framework_MockObject_Builder_InvocationMocker::method`
![PHPUnit_Framework_MockObject_Builder_InvocationMocker::method](https://raw.githubusercontent.com/maxfilatov/phpuaca/master/img/invocation_mocker_method.png)

List of supported methods
-------------------------
* **PHPUnit**
 * `PHPUnit_Framework_MockObject_MockBuilder::setMethods`
 * `PHPUnit_Framework_TestCase::getMock`
 * `PHPUnit_Framework_TestCase::getMockClass`
 * `PHPUnit_Framework_TestCase::getMockForAbstractClass`
 * `PHPUnit_Framework_TestCase::getMockForTrait`
 * `PHPUnit_Framework_MockObject_Builder_InvocationMocker::method`
* **PHPUnit Helper**
 * `PHPUnit_Helper::getProtectedPropertyValue`
 * `PHPUnit_Helper::setProtectedPropertyValue`
 * `PHPUnit_Helper::callProtectedMethod`
* **Method Mock**
 * `MethodMock::resetMethodCalledStack`
 * `MethodMock::getCalledArgs`
 * `MethodMock::isMethodCalled`
 * `MethodMock::countMethodCalled`
 * `MethodMock::revertMethod`
 * `MethodMock::interceptMethodByCode`
 * `MethodMock::interceptMethod`
 * `MethodMock::mockMethodResult`
 * `MethodMock::mockMethodResultByMap`
 * `MethodMock::revertMethodResult`
 * `MethodMock::callProtectedMethod`
