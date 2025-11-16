import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import InputField from "../../shared/InputField";
import Spinners from "../../shared/Spinners";
import Button from "@mui/material/Button";
import { useDispatch, useSelector } from "react-redux";
import {
  addNewProductFromDashboard,
  fetchCategories,
  updateProductFromDashboard,
} from "../../../store/actions";
import toast from "react-hot-toast";
import SelectTextField from "../../shared/SelectTextField";
import { useSearchParams } from "react-router-dom";
import Skeleton from "../../shared/Skeleton";
import ErrorPage from "../../shared/ErrorPage";
const AddProductForm = ({ setOpen, product, update = false }) => {
  const [loader, setLoader] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState();
  const dispatch = useDispatch();
  const { categories } = useSelector((state) => state.products);
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
      const sendData = {
        ...data,
        categoryId: selectedCategory.categoryId,
      };
      dispatch(
        addNewProductFromDashboard(sendData, toast, reset, setLoader, setOpen)
      );
    } else {
      const sendData = {
        ...data,
        id: product.id,
      };
      dispatch(
        updateProductFromDashboard(sendData, toast, reset, setLoader, setOpen)
      );
    }
  };

  useEffect(() => {
    if (update && product) {
      setValue("productName", product?.productName);
      setValue("price", product?.price);
      setValue("quantity", product?.quantity);
      setValue("specialPrice", product?.specialPrice);
      setValue("description", product?.description);
      setValue("discount", product?.discount);
    }
  }, [update, product]);

  useEffect(() => {
    if (!update) {
      dispatch(fetchCategories());
    }
  }, [dispatch, update]);

  useEffect(() => {
    if (!categoryLoader && categories) {
      setSelectedCategory(categories[0]);
    }
  }, [categories, categoryLoader]);

  if (categoryLoader) return <Skeleton />;
  if (errorMessage) return <ErrorPage message={errorMessage} />;
  return (
    <div className="py-5 relative h-full">
      <form className="space-y-4" onSubmit={handleSubmit(saveProductHandler)}>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="Product Name"
            required
            id="productName"
            text="text"
            message="This field is required*"
            placeholder="Product Name"
            register={register}
            errors={errors}
          />
          {!update && (
            <SelectTextField
              label="Select Category"
              select={selectedCategory}
              setSelect={setSelectedCategory}
              lists={categories}
            />
          )}
        </div>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="Price"
            required
            id="price"
            text="number"
            message="This field is required*"
            placeholder="Product Price"
            register={register}
            errors={errors}
          />
          <InputField
            label="Quantity"
            required
            id="quantity"
            text="number"
            message="This field is required*"
            placeholder=" Product Quantity"
            register={register}
            errors={errors}
          />
        </div>
        <div className="flex md:flex-row flex-col gap-4 w-full">
          <InputField
            label="Special Price"
            required
            id="specialPrice"
            text="number"
            message="This field is required*"
            placeholder="Special Price"
            register={register}
            errors={errors}
          />
          <InputField
            label="Discount"
            required
            id="discount"
            text="number"
            message="This field is required*"
            placeholder="Discount"
            register={register}
            errors={errors}
          />
        </div>
        <div className="flex flex-col gap-4 w-full">
          <label
            className="font-semibold text-sm text-slate-800"
            htmlFor="desc"
          >
            Description
          </label>

          <textarea
            rows={5}
            placeholder="Add product description"
            className={`px-4 py-2  w-full border outline-none bg-transparent text-slate-800 rounded-md ${
              errors["description"]?.message
                ? "border-red-500"
                : "border-slate-700"
            }`}
            maxLength={255}
            {...register("description", {
              required: { value: true, message: "Description is required*" },
            })}
          />
          {errors["description"]?.message && (
            <p className="text-sm font-semibold text-red-600 mt-0">
              {errors["description"]?.message}
            </p>
          )}
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
              "Save"
            )}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default AddProductForm;
