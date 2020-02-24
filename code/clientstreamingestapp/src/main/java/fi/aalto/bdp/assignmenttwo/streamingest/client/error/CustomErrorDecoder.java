package fi.aalto.bdp.assignmenttwo.streamingest.client.error;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            default:
                return new Exception(response.toString());
        }
    }
}
