import React, { useState } from "react";
import { MdFormatListBulletedAdd } from "react-icons/md";
import { adminCategoryTable } from "../../helper/tableColumn";
import useDashboardCategoryFilter from "../../../hooks/useCategoryFilter";
import AddCategoryForm from "./AddCategoryForm";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import Loader from "../../shared/Loader";
import { FaBoxOpen } from "react-icons/fa";
import { DataGrid } from "@mui/x-data-grid";
import Modal from "../../shared/Modal";
import DeleteModal from "../../shared/DeleteModal";
import toast from "react-hot-toast";
import { deleteCategory } from "../../../store/actions";

const Category = () => {
  const [openAddModal, setOpenAddModal] = useState(false);
  const { categories, pagination } = useSelector((state) => state.products);
  const [loader, setLoader] = useState(false);
  const [openUpdateModal, setOpenUpdateModal] = useState(false);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState("");
  const emptyCategory = !categories || categories?.length === 0;
  const [currentPage, setCurrentPage] = useState(
    pagination?.pageNumber + 1 || 1
  );
  const { isLoading, errorMessage } = useSelector((state) => state.errors);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const params = new URLSearchParams(searchParams);
  const pathname = useLocation().pathname;

  useDashboardCategoryFilter();
  const tableRecords = categories?.map((item) => {
    return {
      id: item.categoryId,
      categoryName: item.categoryName,
      version: item.version,
    };
  });

  const handlePaginationChange = (paginationModel) => {
    const page = paginationModel.page + 1;
    setCurrentPage(page);
    params.set("page", page.toString());
    navigate(`${pathname}?${params}`);
  };

  const handleEdit = (category) => {
    setSelectedCategory(category);
    setOpenUpdateModal(true);
  };
  const handleDelete = (category) => {
    setSelectedCategory(category);
    setOpenDeleteModal(true);
  };

  const onDeleteHandler = () => {
    dispatch(
      deleteCategory(toast, setLoader, selectedCategory?.id, setOpenDeleteModal)
    );
  };

  return (
    <div>
      <div className="pt-6 pb-10 flex justify-end">
        <button
          onClick={() => setOpenAddModal(true)}
          className="bg-custom-blue hover:bg-blue-800 text-white font-semibold py-2 px-4 flex items-center gap-2 rounded-md shadow-md transition-colors hover:text-slate-300 duration-300"
        >
          <MdFormatListBulletedAdd className="text-xl" />
          Add Category
        </button>
      </div>

      {!emptyCategory && (
        <h1 className="text-slate-800 text-center text-3xl font-bold pb-6 uppercase">
          All Categories
        </h1>
      )}
      {isLoading ? (
        <Loader />
      ) : (
        <>
          {emptyCategory ? (
            <div className="flex flex-col items-center justify-center text-gray-600">
              <FaBoxOpen size={50} className="mb-3" />
              <h2 className="text-2xl font-semibold">
                No categories available
              </h2>
            </div>
          ) : (
            <div className="max-w-full">
              <DataGrid
                className="w-full"
                rows={tableRecords}
                columns={adminCategoryTable(handleEdit, handleDelete)}
                paginationMode="server"
                rowCount={pagination.totalElements || 0}
                initialState={{
                  pagination: {
                    paginationModel: {
                      pageSize: pagination?.pageSize || 10,
                      page: currentPage - 1,
                    },
                  },
                }}
                onPaginationModelChange={handlePaginationChange}
                disableRowSelectionOnClick
                disableColumnResize
                pageSizeOptions={[pagination?.pageSize || 10]}
                pagination
                paginationOptions={{
                  showFirstButton: true,
                  showLastButton: true,
                  showNextButton: currentPage === pagination.totalPages,
                }}
              />
            </div>
          )}
        </>
      )}

      <Modal
        open={openUpdateModal || openAddModal}
        setOpen={openUpdateModal ? setOpenUpdateModal : setOpenAddModal}
        title={openUpdateModal ? "Update Category" : "Add Category"}
      >
        <AddCategoryForm
          setOpen={openUpdateModal ? setOpenUpdateModal : setOpenAddModal}
          category={selectedCategory}
          update={openUpdateModal}
        />
      </Modal>
      <DeleteModal
        open={openDeleteModal}
        setOpen={setOpenDeleteModal}
        loader={loader}
        title="Delete Category"
        onDeleteHandler={onDeleteHandler}
      />
    </div>
  );
};

export default Category;
