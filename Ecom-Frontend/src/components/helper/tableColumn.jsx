import { FaEdit, FaEye, FaImage, FaTrashAlt } from "react-icons/fa";
import { MdOutlineEmail } from "react-icons/md";
export const adminOrderTableColumn = (handleEdit) => [
  {
    sortable: false,
    disableColumnMenu: true,
    field: "id",
    headerName: "orderId",
    minWidth: 180,
    headerAlign: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="text-center">Order ID</span>,
  },
  {
    field: "email",
    minWidth: 220,
    sortable: false,
    headerAlign: "center",
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => <span className="text-black font-semibold">Email</span>,
  },
  {
    field: "totalAmount",
    minWidth: 150,
    headerAlign: "center",
    sortable: true,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => (
      <span className="text-black font-semibold">Total Amount</span>
    ),
  },
  {
    field: "orderDate",
    minWidth: 150,
    sortable: false,
    headerAlign: "center",
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => (
      <span className="text-black font-semibold">Order Date</span>
    ),
  },
  {
    field: "status",
    minWidth: 120,
    sortable: false,
    headerAlign: "center",
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => (
      <span className="text-black font-semibold">Status</span>
    ),
  },
  {
    field: "action",
    minWidth: 140,
    sortable: false,
    headerAlign: "center",
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => (
      <span className="text-black font-semibold">Action</span>
    ),
    renderCell: (params) => {
      return (
        <div className="flex justify-center items-center space-x-2 h-full pt-2">
          <button
            onClick={() => handleEdit(params.row)}
            className="flex items-center bg-blue-500 text-white px-4 h-9 rounded-md "
          >
            <FaEdit className="mr-2" />
            Edit
          </button>
        </div>
      );
    },
  },
];
export const adminProductTableColumn = (
  handleEdit,
  handleDelete,
  handleImageUpload,
  handleProductView
) => [
  {
    disableColumnMenu: true,
    sortable: false,
    field: "id",
    headerName: "ID",
    minWidth: 200,
    headerAlign: "center",
    align: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="text-center">ProductID</span>,
  },
  {
    disableColumnMenu: true,
    field: "productName",
    headerName: "Product Name",
    align: "center",
    width: 260,
    editable: false,
    sortable: false,
    headerAlign: "center",
    headerClassName: "text-black font-semibold text-center border ",
    cellClassName: "text-slate-700 font-normal border text-center",
    renderHeader: (params) => <span>Product Name</span>,
  },

  {
    disableColumnMenu: true,
    field: "price",
    headerName: "Price",
    minWidth: 200,
    headerAlign: "center",
    align: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="text-center">Price</span>,
  },
  {
    disableColumnMenu: true,
    field: "quantity",
    headerName: "Quantity",
    minWidth: 200,
    headerAlign: "center",
    align: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="text-center">Quantity</span>,
  },
  {
    disableColumnMenu: true,
    field: "specialPrice",
    headerName: "Price",
    minWidth: 200,
    headerAlign: "center",
    align: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => (
      <span className="text-center">Special Price</span>
    ),
  },
  {
    sortable: false,
    field: "description",
    headerName: "Image",
    headerAlign: "center",
    align: "center",
    width: 200,
    editable: false,
    disableColumnMenu: true,
    headerClassName: "text-black font-semibold border ",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="ps-10">Description</span>,
  },
  {
    sortable: false,
    field: "image",
    headerName: "Image",
    headerAlign: "center",
    align: "center",
    width: 200,
    editable: false,
    disableColumnMenu: true,
    headerClassName: "text-black font-semibold border ",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="ps-10">Image</span>,
  },

  {
    field: "action",
    headerName: "Action",
    headerAlign: "center",
    editable: false,
    headerClassName: "text-black font-semibold text-center",
    cellClassName: "text-slate-700 font-normal",
    sortable: false,
    width: 400,
    renderHeader: (params) => <span>Action</span>,
    renderCell: (params) => {
      return (
        <div className="flex justify-center items-center space-x-2 h-full pt-2">
          <button
            onClick={() => handleImageUpload(params.row)}
            className="flex items-center bg-green-500 hover:bg-green-600 text-white px-4 h-9 rounded-md"
          >
            <FaImage className="mr-2" />
            Image
          </button>
          <button
            onClick={() => handleEdit(params.row)}
            className="flex items-center bg-blue-500 text-white px-4 h-9 rounded-md "
          >
            <FaEdit className="mr-2" />
            Edit
          </button>

          <button
            onClick={() => handleDelete(params.row)}
            className="flex items-center bg-red-500 text-white px-4   h-9 rounded-md"
          >
            <FaTrashAlt className="mr-2" />
            Delete
          </button>
          <button
            onClick={() => handleProductView(params.row)}
            className="flex items-center bg-slate-800 text-white px-4   h-9 rounded-md"
          >
            <FaEye className="mr-2" />
            View
          </button>
        </div>
      );
    },
  },
];

export const adminCategoryTable = (handleEdit, handleDelete) => [
  {
    disableColumnMenu: true,
    sortable: false,
    field: "id",
    headerName: "CategoryId",
    minWidth: 200,
    headerAlign: "center",
    align: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="text-center">CategoryID</span>,
  },
  {
    disableColumnMenu: true,
    field: "categoryName",
    headerName: "Category Name",
    align: "center",
    width: 260,
    editable: false,
    sortable: false,
    headerAlign: "center",
    headerClassName: "text-black font-semibold text-center border ",
    cellClassName: "text-slate-700 font-normal border text-center",
    renderHeader: (params) => <span>Category Name</span>,
  },

  {
    field: "action",
    headerName: "Action",
    headerAlign: "center",
    editable: false,
    headerClassName: "text-black font-semibold text-center",
    cellClassName: "text-slate-700 font-normal",
    sortable: false,
    width: 400,
    renderHeader: (params) => <span>Action</span>,
    renderCell: (params) => {
      return (
        <div className="flex justify-center items-center space-x-2 h-full pt-2">
          <button
            onClick={() => handleEdit(params.row)}
            className="flex items-center bg-blue-500 text-white px-4 h-9 rounded-md "
          >
            <FaEdit className="mr-2" />
            Edit
          </button>

          <button
            onClick={() => handleDelete(params.row)}
            className="flex items-center bg-red-500 text-white px-4   h-9 rounded-md"
          >
            <FaTrashAlt className="mr-2" />
            Delete
          </button>
        </div>
      );
    },
  },
];

export const sellerDashboardTableColumn = () => [
  {
    sortable: false,
    disableColumnMenu: true,
    field: "id",
    headerName: "sellerId",
    minWidth: 180,
    headerAlign: "center",
    editable: false,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: (params) => <span className="text-center">Seller ID</span>,
  },
  {
    field: "username",
    minWidth: 150,
    headerAlign: "center",
    headerName: "userName",
    sortable: true,
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => (
      <span className="text-black font-semibold">User Name</span>
    ),
  },

  {
    field: "email",
    minWidth: 220,
    sortable: false,
    headerName: "Email",
    headerAlign: "center",
    headerClassName: "text-black font-semibold border",
    cellClassName: "text-slate-700 font-normal border",
    renderHeader: () => <span className="text-black font-semibold">Email</span>,
    renderCell: (params) => {
      return (
        <div className="flex items-center justify-center gap-1">
          <span>
            <MdOutlineEmail className="text-slate-700 text-lg" />
          </span>
          <span>{params?.row?.email}</span>
        </div>
      );
    },
  },
];
