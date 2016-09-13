# sms-sender

A simple way for sending SMS via [sms.ru](http://sms.ru) with Java.

At first sign up on sms.ru and receive your api_id, login, password which would be needed for authentication your requests. [sms.ru API description](https://sms.ru/?panel=api), unfortunatelly the page is still only in Russian.

### Configuration

Use [SenderServiceConfigurationBuilder](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderServiceConfigurationBuilder.java) to create [SenderServiceConfiguration](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderServiceConfiguration.java) which should be passed as argument into constructor of [SenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderService.java) or [AsyncSenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/AsyncSenderService.java).

There are necessary fields in configuration, which are used by authentication provider you picked. Authentication providers are implementations of [AuthProvider](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/smsru/auth/AuthProvider.java), by default [DefaultAuthProvider](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/smsru/auth/DefaultAuthProvider.java) is used.

CloseableHttpClient, [AuthProvider](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/smsru/auth/AuthProvider.java) class to use, [SenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderService.java) to use in the [AsyncSenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/AsyncSenderService.java), list of retry policies and other params are arbitrary in [SenderServiceConfiguration](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderServiceConfiguration.java).

[SenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderService.java) and [AsyncSenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/AsyncSenderService.java) throw IllegalStateException during construction if some necessary fields are missing.

Configuration parameters could be set from java code or loaded from the FileInputStream or Properties object
```
SenderServiceConfiguration senderConfiguration = SenderServiceConfigurationBuilder.create()
      .load(new FileInputStream("test.properties"))
      .build();
```

### Sync and Async senders

Library has sync and async services 
- [SenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderService.java) executes the request parse response and retries request if needed in the same thread
- [AsyncSenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/AsyncSenderService.java) works asynchronously using ExecutorService from the configuration or ```Executors.newSingleThreadExecutor()``` if no ExecutorService was provided. Async service has two ```execute(...)``` methods: the first one returns Future<R>, the second has an optional argument array of [ApiCallback](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/ApiCallback.java) which would be called after receiving and parsing response from the API. 

For further information look at the "Usage examples" section located at the end of the file.


### API requests

All requests extend [ApiRequest](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/ApiRequest.java) class, which has number of execution attempts, [InvocationStatus] (https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/InvocationStatus.java) status, exception which would be set iff status.isAbnormal() and attachment.

Attachment could be usefull if you use AsyncSenderService with callbacks and want to use some additional information related with request in callbacks, e.g. id of DB row which corresponds to the SMS sending.


### API responses
Responses extend [ApiResponse](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/ApiResponse.java) class, which contains plain response of the remote system (if this option is enabled in configuration) and response status specific to remote API.


### SMS.RU api requests

- [SMSRuSendRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/send/SMSRuSendRequest.java) — sends a message to a single or multiple receivers.
- [SMSRuBatchSendRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/send/SMSRuBatchSendRequest.java) — sends different messages to a single or different receivers.
- [SMSRuSendersRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/senders/SMSRuSendersRequest.java)
- [SMSRuStatusRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/status/SMSRuStatusRequest.java) — checks status of previously sent message.
- [SMSRuCostRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/cost/SMSRuCostRequest.java) — determines the price of the message for the selected receiver. 
- [SMSRuStopListAddRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/stoplist/add/SMSRuStopListAddRequest.java) — adds phone to the black list.
- [SMSRuStopListDeleteRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/stoplist/delete/SMSRuStopListDeleteRequest.java) — deletes phone from the black list.
- [SMSRuStopListGetRequest](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/stoplist/get/SMSRuStopListGetRequest.java) — fetches the whole black list.


### SMS.RU response status
[SMSRuResponseStatus](https://github.com/dezhik/sms-sender/tree/master/src/main/java/ru/dezhik/sms/sender/api/smsru/SMSRuResponseStatus.java) is the enum containing possible response statuses of sms.ru API. Each status has a comment explaining the situation then it could be received. For further and more accurate information go to the [sms.ru API description page](https://sms.ru/?panel=api).


### Authentication

There are 3 ways for authenticating your API requests:
- [DefaultAuthProvider](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/smsru/auth/DefaultAuthProvider.java) — uses api_id. _This provider is used by default_
- [PasswordAuthProvider](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/smsru/auth/PasswordAuthProvider.java) — uses password and login.
- [TokenAuthProvider](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/smsru/auth/TokenAuthProvider.java) — fetches api token using api_id and then authenticates each request with SHA-512 digest of password and token.


### Retry policies

You can add policies/conditions under which the request should be retried and specify a delay before next retry. All retry policies should implement [RetryPolicy] (https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/RetryPolicy.java) interface. [SenderServiceConfigurationBuilder](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderServiceConfigurationBuilder.java) ```addRetryPolicy(RetryPolicy)```

By default there is only a [NetworkErrorRetryPolicy](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/NetworkErrorRetryPolicy.java) 


## Service shutdown

Don't forget to shutdown sender service during shutting down your application, use ```shutdown()``` for this. 

#### This is especially necessary if you use [AsyncSenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/AsyncSenderService.java), which has ExecutorService under the hood. 

There is a special setting for AsyncSenderService ```shutdown()``` in [SenderServiceConfiguration](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderServiceConfiguration.java), ```asyncTerminationTimeoutMs``` which specifies how much time to wait befare call ```shutdownNow()``` on the ExecutorService if it still has running threads.


### Usage examples

The most efficient way to use the library is to create [SenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/SenderService.java) or [AsyncSenderService](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/AsyncSenderService.java) instance once then share it between threads and use numerous times. 

Synchronous execution example:

```
SenderService sender = new SenderService(senderConfiguration);
SMSRuCostRequest sendRequest = new SMSRuCostRequest();
sendRequest.setReceiver("+71234567890");
sendRequest.setText("Hello world");
SMSRuSendResponse sendResponse = sender.execute(sendRequest);
if (sendRequest.getStatus() == InvocationStatus.SUCCESS) {
    ;//request was executed successfully now you can handle sendResponse
}
...
sender.shutdown();
```
   
Simple asynchronous request execution:

```     
AsyncSenderService asyncSender = new AsyncSenderService(senderConfiguration);
SMSRuCostRequest sendRequest = new SMSRuCostRequest();
sendRequest.setReceiver("+71234567890");
sendRequest.setText("Hello world");
Future<SMSRuSendResponse> sendFuture = asyncSender.execute(sendResponse);
...
SMSRuSendResponse sendResponse = sendFuture.get();
if (sendRequest.getStatus() == InvocationStatus.SUCCESS) {
    ;//request was executed successfully now you can handle sendResponse
}
...
asyncSender.shutdown();
```

Asynchronous request execution with callbacks, [ApiCallback](https://github.com/dezhik/sms-sender/blob/master/src/main/java/ru/dezhik/sms/sender/api/ApiCallback.java):

```
AsyncSenderService asyncSender = new AsyncSenderService(senderConfiguration);
SMSRuCostRequest sendRequest = new SMSRuCostRequest();
sendRequest.setReceiver("+71234567890");
sendRequest.setText("Hello world");
ApiCallback successCallback = new ApiCallback() {
    @Override
    public boolean apply(ApiRequest request, SimpleResponse response) {
        return request.getStatus() == InvocationStatus.SUCCESS;
    }
    
    @Override
    public void execute(ApiRequest request, SimpleResponse response) {
        //handle successful request, e.g. update sms status in your database or smth else
    }
};

ApiCallback failCallback = new ApiCallback() {
    @Override
    public boolean apply(ApiRequest request, SimpleResponse response) {
        return request.getStatus() != InvocationStatus.SUCCESS;
    }

    @Override
    public void execute(ApiRequest request, SimpleResponse response) {
        //handle failed request, e.g. print error or try to send email instead of sms
    }
};

asyncSender.execute(sendResponse, successCallback, failCallback);
...
asyncSender.shutdown();
```