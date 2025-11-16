import React, { useState } from "react";
import { TiUserAdd } from "react-icons/ti";
import { sellerDashboardTableColumn } from "../../helper/tableColumn";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import Loader from "../../shared/Loader";
import { FaBoxOpen } from "react-icons/fa";
import { DataGrid } from "@mui/x-data-grid";
import Modal from "../../shared/Modal";
import useDashboardSellerFilter from "../../../hooks/useSellerFilter";
import AddSellerForm from "./AddSellerForm";

const Category = () => {
  const [openAddModal, setOpenAddModal] = useState(false);
  const { sellers, pagination } = useSelector((state) => state.sellers);
  const [loader, setLoader] = useState(false);
  const emptySeller = !sellers || sellers?.length === 0;
  const [currentPage, setCurrentPage] = useState(
    pagination?.pageNumber + 1 || 1
  );
  const { isLoading, errorMessage } = useSelector((state) => state.errors);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const params = new URLSearchParams(searchParams);
  const pathname = useLocation().pathname;

  useDashboardSellerFilter();
  const tableRecords = sellers?.map((item) => {
    return {
      id: item.userId,
      username: item.userName,
      email: item.email,
    };
  });

  const handlePaginationChange = (paginationModel) => {
    const page = paginationModel.page + 1;
    setCurrentPage(page);
    params.set("page", page.toString());
    navigate(`${pathname}?${params}`);
  };

  return (
    <div>
      <div className="pt-6 pb-10 flex justify-end">
        <button
          onClick={() => setOpenAddModal(true)}
          className="bg-custom-blue hover:bg-blue-800 text-white font-semibold py-2 px-4 flex items-center gap-2 rounded-md shadow-md transition-colors hover:text-slate-300 duration-300"
        >
          <TiUserAdd className="text-xl" />
          Add Seller
        </button>
      </div>

      {!emptySeller && (
        <h1 className="text-slate-800 text-center text-3xl font-bold pb-6 uppercase">
          All Sellers
        </h1>
      )}
      {isLoading ? (
        <Loader />
      ) : (
        <>
          {emptySeller ? (
            <div className="flex flex-col items-center justify-center text-gray-600">
              <FaBoxOpen size={50} className="mb-3" />
              <h2 className="text-2xl font-semibold">No Sellers available</h2>
            </div>
          ) : (
            <div className="max-w-full">
              <DataGrid
                className="w-full"
                rows={tableRecords}
                columns={sellerDashboardTableColumn()}
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

      <Modal open={openAddModal} setOpen={setOpenAddModal} title={"Add Seller"}>
        <AddSellerForm setOpen={setOpenAddModal} />
      </Modal>
    </div>
  );
};

export default Category;
