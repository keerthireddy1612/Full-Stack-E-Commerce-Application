import { Alert, AlertTitle } from "@mui/material";
import React from "react";

const PaypalPayment = () => {
  return (
    <div className="h-96 flex justify-center items-center">
      <Alert severity="warning" variant="filled" style={{ maxWidth: "400px" }}>
        <AlertTitle>PayPal Unavailable</AlertTitle>
        Paypal Payment is unavailable, please use a different payment method.
      </Alert>
    </div>
  );
};

export default PaypalPayment;
