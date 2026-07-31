# StockSim
Stock Simulator 



## TO DO:

- Remove Trade Order Status (or keep it if I want to add limit orders )
- fix tests
- add to frontend deposit and trade orders DONE
- add holdings to frontend
- zoomable graph IN PROGRESS
- slider for spending
- fix tradeorder controller 

## long-term Features:

- Limit Orders ("buy stock when price reaches a certain level")
- Sectors and Random events


2026-07-30T17:25:40.063+01:00 ERROR 16004 --- [nio-8080-exec-8] o.h.engine.jdbc.spi.SqlExceptionHelper   : ERROR: duplicate key value violates unique constraint "stocks_company_id_key"
Detail: Key (company_id)=(1) already exists.
INTERNAL ERROR: could not execute statement [ERROR: duplicate key value violates unique constraint "stocks_company_id_key"
Detail: Key (company_id)=(1) already exists.] [insert into stocks (company_id,price) values (?,?)]; SQL [insert into stocks (company_id,price) values (?,?)]; constraint [stocks_company_id_key]
