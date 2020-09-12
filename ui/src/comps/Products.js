import React from 'react';
import 'materialize-css/dist/css/materialize.min.css';
import {
    Link
} from "react-router-dom";
import ProductRow from "./ProductRow";


export class ProductCategoryRow extends React.Component {
    render() {
        const category = this.props.category;
        return (
            <tr>
                <th colSpan="4">
                    {category}
                </th>
            </tr>
        );
    }
}

export class RedirectButton extends React.Component{
    render() { return <Link to={this.props.to}><button className="btn btn-primary z-depth-2 hoverable">Details</button></Link>; }
}



export class ProductTable extends React.Component {

    render() {
        const filterProduct = this.props.filterProduct;
        const filterCategory = this.props.filterCategory;
        const inStockOnly = this.props.inStockOnly;
        const saleOnly = this.props.saleOnly;

        const rows = [];
        let lastCategory = null;

        this.props.products.forEach((product) => {
            if (product.name.indexOf(filterProduct) === -1) {
                return;
            }
            if (product.category.indexOf(filterCategory) === -1) {
                return;
            }
            if (inStockOnly && product.stock <= 0) {
                return;
            }
            if (saleOnly && product.promotion <= 0) {
                return;
            }
            if (product.category !== lastCategory) {
                rows.push(
                    <ProductCategoryRow
                        category={product.category}
                        key={product.category} />
                );
            }
            rows.push(
                <ProductRow
                    product={product}
                    key={product.name}
                />
            );
            lastCategory = product.category;
        });

        return (
            <table className="centered">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Price</th>
                    <th> </th>
                    <th> </th>
                </tr>
                </thead>
                <tbody>
                    {rows}
                </tbody>
            </table>
        );
    }
}

export class SearchBar extends React.Component {
    constructor(props) {
        super(props);
        this.handleFilterProductChange = this.handleFilterProductChange.bind(this);
        this.handleFilterCategoryChange = this.handleFilterCategoryChange.bind(this);
        this.handleInStockChange = this.handleInStockChange.bind(this);
        this.handleSaleChange = this.handleSaleChange.bind(this);
    }

    handleFilterProductChange(e) {
        this.props.onFilterProductChange(e.target.value);
    }

    handleFilterCategoryChange(e) {
        this.props.onFilterCategoryChange(e.target.value);
    }

    handleInStockChange(e) {
        this.props.onInStockChange(e.target.checked);
    }

    handleSaleChange(e) {
        this.props.onSaleChange(e.target.checked);
    }

    render() {

        return (
            <div className="container">
            <form>
                <input
                    type="text"
                    placeholder="Search products..."
                    value={this.props.filterProduct}
                    onChange={this.handleFilterProductChange}
                />
                <input
                    type="text"
                    placeholder="Search category..."
                    value={this.props.filterCategory}
                    onChange={this.handleFilterCategoryChange}
                />
                    <div className="switch">
                    <label>
                        <input type="checkbox"
                               checked={this.props.inStockOnly}
                               onChange={this.handleInStockChange}/>
                        <span className="lever">''</span>
                        In stock
                    </label>
                    </div>
                    <div className="switch">
                    <label>
                        <input type="checkbox"
                               checked={this.props.saleOnly}
                               onChange={this.handleSaleChange}/>
                        <span className="lever">''</span>
                        On sale
                    </label>
                    </div>
            </form>
            </div>
        );
    }
}

export class Products extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            filterProduct: '',
            filterCategory: '',
            inStockOnly: true,
            saleOnly: false,
            details: false,
            products: []
        };
    }

    componentDidMount() {
        fetch("http://localhost:9000/api/products/extended")
            .then(res => res.json())
            .then(
                (result) => {
                    result.sort((a, b) => a.categoryId - b.categoryId);
                    this.setState({
                        products: result,
                    });
                }
            )

        this.handleFilterProductChange = this.handleFilterProductChange.bind(this);
        this.handleFilterCategoryChange = this.handleFilterCategoryChange.bind(this);
        this.handleInStockChange = this.handleInStockChange.bind(this);
        this.handleSaleChange = this.handleSaleChange.bind(this);
    }

    handleFilterProductChange(filterProduct) {
        this.setState({
            filterProduct: filterProduct
        });
    }

    handleFilterCategoryChange(filterCategory) {
        this.setState({
            filterCategory: filterCategory
        });
    }

    handleInStockChange(inStockOnly) {
        this.setState({
            inStockOnly: inStockOnly
        })
    }

    handleSaleChange(saleOnly) {
        this.setState({
            saleOnly: saleOnly
        })
    }

    render() {
        return (
            <div className="container">
            <div>
                <SearchBar
                    filterProduct={this.state.filterProduct}
                    onFilterProductChange={this.handleFilterProductChange}
                    filterCategory={this.state.filterCategory}
                    onFilterCategoryChange={this.handleFilterCategoryChange}
                    inStockOnly={this.state.inStockOnly}
                    saleOnly={this.state.saleOnly}
                    onInStockChange={this.handleInStockChange}
                    onSaleChange={this.handleSaleChange}
                />
                <ProductTable
                    products={this.state.products}
                    filterProduct={this.state.filterProduct}
                    filterCategory={this.state.filterCategory}
                    inStockOnly={this.state.inStockOnly}
                    saleOnly={this.state.saleOnly}
                />
            </div>
            </div>
        );
    }
}
