import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import Spinners from "../../shared/Spinners";
import {
  addNewCategoryFromDashboard,
  addNewDashboardSeller,
} from "../../../store/actions";
import InputField from "../../shared/InputField";
import { Button } from "@mui/material";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

const AddSellerForm = ({ setOpen }) => {
  const [loader, setLoader] = useState(false);
  const dispatch = useDispatch();
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({
    mode: "onTouched",
  });
  const addSellerHandler = (data) => {
    const sendData = {
      ...data,
      role: ["seller"],
    };

    dispatch(addNewDashboardSeller(sendData, toast, reset, setOpen, setLoader));
  };

  return (
    <div className="py-5 relative h-full">
      <form className="space-y-4" onSubmit={handleSubmit(addSellerHandler)}>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="User Name"
            required
            id="username"
            text="text"
            message="This field is required*"
            placeholder="Enter username"
            register={register}
            errors={errors}
          />
        </div>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="Email"
            required
            id="email"
            text="text"
            message="This field is required*"
            placeholder="Enter username"
            register={register}
            errors={errors}
          />
        </div>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="Password"
            required
            id="password"
            text="text"
            message="This field is required*"
            placeholder="Enter password"
            register={register}
            errors={errors}
          />
        </div>

        <div className="flex w-full justify-between items-center absolute bottom-14">
          <Button
            disabled={loader}
            onClick={() => setOpen(false)}
            variant="outlined"
            className="text-white py-2.5 px-4 text-sm font-medium"
          >
            Cancel
          </Button>
          <Button
            disabled={loader}
            type="submit"
            variant="contained"
            color="primary"
            className="bg-custom-blue text-white py-2.5 px-4 text-sm font-medium"
          >
            {loader ? (
              <div className="flex gap-2 items-center">
                <Spinners />
                Loading...
              </div>
            ) : (
              "Add New Seller"
            )}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default AddSellerForm;
