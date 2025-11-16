import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import Skeleton from "../../shared/Skeleton";
import ErrorPage from "../../shared/ErrorPage";
import Spinners from "../../shared/Spinners";
import {
  addNewCategoryFromDashboard,
  updateCategoryFromDashboard,
} from "../../../store/actions";
import InputField from "../../shared/InputField";
import { Button } from "@mui/material";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

const AddCategoryForm = ({ setOpen, category, update = false }) => {
  const [loader, setLoader] = useState(false);
  const dispatch = useDispatch();
  const { categoryLoader, errorMessage } = useSelector((state) => state.errors);
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({
    mode: "onTouched",
  });
  const saveProductHandler = (data) => {
    if (!update) {
      dispatch(
        addNewCategoryFromDashboard(data, toast, reset, setLoader, setOpen)
      );
    } else {
      const sendData = {
        ...data,
        id: category.id,
      };
      dispatch(
        updateCategoryFromDashboard(sendData, toast, reset, setLoader, setOpen)
      );
    }
  };

  useEffect(() => {
    if (update && category) {
      setValue("categoryName", category?.categoryName);
    }
  }, [update, category]);

  if (categoryLoader) return <Skeleton />;
  if (errorMessage) return <ErrorPage message={errorMessage} />;
  return (
    <div className="py-5 relative h-full">
      <form className="space-y-4" onSubmit={handleSubmit(saveProductHandler)}>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="Category Name"
            required
            id="categoryName"
            text="text"
            message="This field is required*"
            placeholder="Category Name"
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
            ) : update ? (
              "Update"
            ) : (
              "Save"
            )}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default AddCategoryForm;
