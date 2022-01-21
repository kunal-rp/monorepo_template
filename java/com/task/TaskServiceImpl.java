package com.task;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.lang.Integer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream; 
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import com.google.inject.Inject;
import com.google.rpc.Status;
import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.protobuf.StatusProto;
import com.google.rpc.Code;
import io.grpc.stub.StreamObserver;
import com.task.TaskServiceProto.ActionRequest;
import com.task.TaskServiceProto.ActionResponse;

public class TaskServiceImpl extends TaskServiceGrpc.TaskServiceImplBase {

    @Override
    public void someAction(ActionRequest req, StreamObserver<ActionResponse> responseObserver) {

        //get userid from context : Constants.USER_ID_CTX_KEY.get()

      responseObserver.onNext(
            ActionResponse.newBuilder()
                .addResultData("result data 1")
                .addResultData("result data 2")
                .addResultData("result data 3")
                .build());

      responseObserver.onCompleted();

    }

}